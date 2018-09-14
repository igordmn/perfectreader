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
    private val job = Job()

    override fun dispose() {
        job.cancel()
        disposables.dispose()
    }

    fun <T> observableDisposable(initial: T, dispose: (T) -> Unit = {}) = ObservableDisposableDelegate(initial, dispose)
    fun <T : Disposable?> observableDisposable(initial: T) = observableDisposable(initial, dispose = { it?.dispose() })
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
            value: T,
            private val dispose: (T) -> Unit
    ) : ReadWriteProperty2<Any?, T> {
        private var observable by observable(value)

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

        inner class Cache(val value: T, private val subscription: Disposable): Disposable {
            override fun dispose() {
                dispose(value)
                subscription.dispose()
            }
        }
    }

    inner class AsyncComputedDelegate<T>(
            private val context: CoroutineContext,
            private val resetOnRecompute: Boolean,
            private val compute: suspend CoroutineScope.() -> T?,
            private val dispose: (T) -> Unit
    ) : ReadOnlyProperty2<Any?, T?> {
        private lateinit var job: Job
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
            val (job, subscription) = subscribeOnchange(context, action = {
                observable = compute()
            }, onchange = {
                stop()
                start()
            })
            this.job = job
            this.subscription = subscription
        }

        private fun stop() {
            job.cancel()
            subscription.dispose()
            if (resetOnRecompute)
                observable = null
        }

        override val value: T? get() = observable
    }
}