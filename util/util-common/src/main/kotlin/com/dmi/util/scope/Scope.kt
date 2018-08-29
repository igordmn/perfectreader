package com.dmi.util.scope

import com.dmi.util.coroutine.threadContext
import com.dmi.util.lang.threadLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Thread.currentThread
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Scoped : Disposable {
    val scope: Scope

    override fun dispose() = Unit

    class Impl : Scoped {
        override val scope = Scope()
        override fun dispose() = scope.dispose()
    }
}

class Scope : Disposable {
    companion object {
        private var callContext: CallContext? by threadLocal(null)

        private class CallContext {
            val createdObservables = HashSet<Scope.ObservableDelegate<*>>()
            val calledObservables = LinkedHashSet<Scope.ObservableDelegate<*>>()
            val dependencies get() = calledObservables.filter { !createdObservables.contains(it) }

            fun <T> use(action: () -> T): T {
                val oldCallContext = callContext
                Scope.callContext = this
                try {
                    return action()
                } finally {
                    Scope.callContext = oldCallContext
                }
            }
        }

        /**
         * call [action] and intercept all called scoped values. when any of this values changed, event will be emitted
         */
        fun onchange(action: () -> Unit): Event {
            val callContext = CallContext()
            callContext.use(action)
            return object : Event {
                override fun subscribe(action: () -> Unit): Disposable {
                    val disposables = Disposables()
                    callContext.dependencies.forEach {
                        disposables += it.onchange.subscribe(action)
                    }
                    return disposables
                }
            }
        }
    }

    private val disposables = Disposables()
    private var disposed = false
    private val thread = currentThread()
    private val job = Job()

    override fun dispose() {
        require(currentThread() == thread)
        job.cancel()
        disposables.dispose()
        disposed = true
    }

    fun <T> value(initial: T, dispose: (T) -> Unit = {}) = VariableDelegate(initial, dispose)
    fun <T : Disposable?> disposable(initial: T) = value(initial, dispose = { it?.dispose() })
    fun <T> cached(compute: () -> T) = CachedDelegate(compute, dispose = {})
    fun <T : Disposable?> cachedDisposable(compute: () -> T) = CachedDelegate(compute, dispose = { it?.dispose() })
    fun <T> cached(recache: Event, compute: () -> T) = EventCachedDelegate(recache, compute, dispose = {})
    fun <T : Disposable?> cachedDisposable(recache: Event, compute: () -> T) = EventCachedDelegate(recache, compute, dispose = { it?.dispose() })

    fun <T> async(
            context: CoroutineContext = threadContext,
            resetOnRecompute: Boolean = true,
            dispose: (T) -> Unit = {},
            compute: suspend CoroutineScope.() -> T?
    ) = AsyncComputedDelegate(context, resetOnRecompute, compute, dispose)

    // todo think how to dispose after cancel
    fun <T : Disposable?> asyncDisposable(
            context: CoroutineContext = threadContext,
            resetOnRecompute: Boolean = true,
            compute: suspend CoroutineScope.() -> T?
    ) = async(context, resetOnRecompute, dispose = { it?.dispose() }, compute = compute)

    fun launch(
            context: CoroutineContext = threadContext,
            run: suspend CoroutineScope.() -> Unit
    ): Job = launch(context, parent = job, block = run)

    abstract class ObservableDelegate<T> : ReadOnlyProperty<Any?, T> {
        abstract val value: T
        abstract val onchange: Event

        init {
            @Suppress("LeakingThis")
            callContext?.createdObservables?.add(this)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            callContext?.calledObservables?.add(this)
            return value
        }
    }

    inner class VariableDelegate<T>(
            value: T,
            private val dispose: (T) -> Unit
    ) : ObservableDelegate<T>(), ReadWriteProperty<Any?, T> {
        override var value: T = value
            get() {
                check(currentThread() == thread)
                return field
            }
            set (value) {
                check(currentThread() == thread)
                dispose(field)
                field = value
                onchange.emit()
            }

        init {
            disposables += object : Disposable {
                override fun dispose() {
                    dispose(value)
                }
            }
        }

        override val onchange = EmittableEvent()

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = super.getValue(thisRef, property)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }
    }

    inner class CachedDelegate<T>(
            private val compute: () -> T,
            private val dispose: (T) -> Unit
    ) : ObservableDelegate<T>() {
        private var cache: Cache? = null

        init {
            disposables += object : Disposable {
                override fun dispose() {
                    cache?.disposable?.dispose()
                    cache?.value?.let(dispose)
                }
            }
        }

        override val value: T
            get() {
                check(currentThread() == thread)
                if (cache == null) {
                    val callContext = CallContext()
                    val value = callContext.use(compute)
                    val disposables = Disposables()
                    callContext.dependencies.forEach {
                        disposables += it.onchange.subscribe {
                            cache?.disposable?.dispose()
                            cache?.value?.let(dispose)
                            cache = null
                            onchange.emit()
                        }
                    }
                    cache = Cache(value, disposables)
                }
                return cache!!.value
            }

        override val onchange = EmittableEvent()

        inner class Cache(val value: T, val disposable: Disposable)
    }

    inner class EventCachedDelegate<T>(
            recache: Event,
            private val compute: () -> T,
            private val dispose: (T) -> Unit
    ) : ObservableDelegate<T>() {
        private var cache: Cache? = null

        init {
            disposables += object : Disposable {
                override fun dispose() {
                    cache?.value?.let(dispose)
                }
            }
            disposables += recache.subscribe {
                cache?.value?.let(dispose)
                cache = null
                onchange.emit()
            }
        }

        override val value: T
            get() {
                check(currentThread() == thread)
                if (cache == null) {
                    val value = CallContext().use {
                        compute()
                    }
                    cache = Cache(value)
                }
                return cache!!.value
            }

        override val onchange = EmittableEvent()

        inner class Cache(val value: T)
    }

    inner class AsyncComputedDelegate<T>(
            private val context: CoroutineContext,
            private val resetOnRecompute: Boolean,
            private val compute: suspend CoroutineScope.() -> T?,
            private val dispose: (T) -> Unit
    ) : ObservableDelegate<T?>() {
        override var value: T? = null
        override val onchange = EmittableEvent()
        private var subscriptions = Disposables()
        private var callContext = CallContext()

        init {
            disposables += object : Disposable {
                override fun dispose() {
                    callContext = CallContext()
                    job.cancel()
                    value?.let(dispose)
                    subscriptions.dispose()
                }
            }
        }

        private fun wrapBlock(block: () -> Unit) {
            callContext.use(block)
            callContext.dependencies.forEach {
                subscriptions += it.onchange.subscribe { restart() }
            }
            callContext.calledObservables.clear()
        }

        private fun wrapContext(context: CoroutineContext): CoroutineContext {
            class WrapContinuation<T>(val cont: Continuation<T>) : Continuation<T> by cont {
                override fun resumeWith(result: SuccessOrFailure<T>) = wrapBlock { cont.resumeWith(result) }
            }

            return context + object : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
                override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
                    val wrapped = WrapContinuation(continuation)
                    return context[ContinuationInterceptor]?.interceptContinuation(wrapped) ?: wrapped
                }
            }
        }

        private var job = job()

        private fun job() = kotlinx.coroutines.launch(wrapContext(context)) {
            require(currentThread() == thread)
            if (value != null)
                value?.let(dispose)
            value = compute()
            onchange.emit()
        }

        private fun restart() {
            callContext = CallContext()
            job.cancel()
            if (resetOnRecompute) {
                value?.let(dispose)
                value = null
            }
            subscriptions.dispose()
            subscriptions = Disposables()
            job = job()
        }
    }
}