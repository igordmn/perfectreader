package com.dmi.util.scope

import com.dmi.util.coroutine.threadContext
import com.dmi.util.lang.ReadOnlyProperty2
import com.dmi.util.lang.ReadWriteProperty2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KMutableProperty0

class Scope : Disposable {
    private val disposables = Disposables()
    private val disposable = disposables.debugIfEnabled()
    private val job = Job()

    override fun dispose() {
        job.cancel()
        disposable.dispose()
    }

    fun <T : Disposable> disposable(value: T) : T {
        disposables += value
        return value
    }

    fun <T> observableDisposable(initial: T, dispose: (T) -> Unit = {}) = ObservableDisposableDelegate(observable(initial), dispose)
    fun <T : Disposable?> observableDisposable(initial: T) = observableDisposable(initial, dispose = { it?.dispose() })

    fun <T> observableDisposableProperty(
            property: KMutableProperty0<T>, dispose: (T) -> Unit = {}
    ) = ObservableDisposableDelegate(observableProperty(property), dispose)

    fun <T : Disposable?> observableDisposableProperty(
            property: KMutableProperty0<T>
    ) = observableDisposableProperty(property, dispose = { it?.dispose() })

    fun <T> cached(compute: () -> T) = CachedDelegate(compute, dispose = {})
    fun <T : Disposable?> cachedDisposable(compute: () -> T) = CachedDelegate(compute, dispose = { it?.dispose() })

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
    ): Job = GlobalScope.launch(context + job, block = run)

    inner class ObservableDisposableDelegate<T>(
            observableDelegate: ReadWriteProperty2<Any, T>,
            private val dispose: (T) -> Unit
    ) : ReadWriteProperty2<Any?, T> {
        private var observable by observableDelegate

        init {
            disposables += object : Disposable {
                override fun dispose() = dispose(value)
            }
        }

        override var value: T
            get() = observable
            set (value) {
                dispose(observable)
                observable = value
            }
    }

    // todo throw exception on recursion
    inner class CachedDelegate<T>(
            private val compute: () -> T,
            private val dispose: (T) -> Unit
    ) : ReadOnlyProperty2<Any?, T> {
        private var observable by observable(Unit)
        private var cache: Cache? = null

        init {
            disposables += object : Disposable {
                override fun dispose() {
                    cache?.dispose()
                }
            }
        }

        override val value: T
            get() {
                if (cache == null) {
                    val (value, event) = onchange(compute)
                    val subscription = event.subscribe {
                        cache?.dispose()
                        cache = null
                        observable = Unit
                    }
                    cache = Cache(value, subscription)
                }
                observable // just call for intercept
                return cache!!.value
            }

        inner class Cache(val value: T, private val subscription: Disposable) : Disposable {
            override fun dispose() {
                dispose(value)
                subscription.dispose()
            }
        }
    }

    // todo throw exception on recursion
    inner class AsyncComputedDelegate<T>(
            private val context: CoroutineContext,
            private val resetOnRecompute: Boolean,
            private val compute: suspend CoroutineScope.() -> T?,
            private val dispose: (T) -> Unit
    ) : ReadOnlyProperty2<Any?, T?> {
        private lateinit var subscription: Disposable
        private var observable by observableDisposable<T?>(null, dispose = { it?.let(dispose) })

        init {
            start()
            disposables += object : Disposable {
                override fun dispose() {
                    subscription.dispose()
                }
            }
        }

        private fun start() {
            val subscription = subscribeOnChange(context, action = {
                // todo exceptions throwing here not break calling thread (probably because of GlobalScope)
                // after fix remove try/catch
                try {
                    compute()
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }, onchange = {
                stop()
                start()
            }, onresult = {
                observable = it
            })
            this.subscription = subscription
        }

        private fun stop() {
            subscription.dispose()
            if (resetOnRecompute)
                observable = null
        }

        override val value: T? get() = observable
    }
}