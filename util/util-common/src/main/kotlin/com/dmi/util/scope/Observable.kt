package com.dmi.util.scope

import com.dmi.util.lang.ReadWriteProperty2
import com.dmi.util.lang.threadLocal
import com.dmi.util.lang.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

private var callContext: CallContext? by threadLocal(null)

private class CallContext {
    val createdEvents = HashSet<Event>()
    val calledEvents = LinkedHashSet<Event>()
    val dependencies get() = calledEvents.filter { !createdEvents.contains(it) }

    fun <T> use(action: () -> T): T {
        val oldCallContext = callContext
        callContext = this
        try {
            return action()
        } finally {
            callContext = oldCallContext
        }
    }

    fun useLaunch(context: CoroutineContext, action: suspend CoroutineScope.() -> Unit, afterBlock: () -> Unit): Job {
        fun wrapBlock(block: () -> Unit) {
            val oldCallContext = callContext
            callContext = this
            try {
                block()
            } finally {
                callContext = oldCallContext
            }
            afterBlock()
        }

        fun wrapContext(context: CoroutineContext): CoroutineContext {
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

        return GlobalScope.launch(wrapContext(context), block = action)
    }

}

fun <T> observable(initial: T) : ReadWriteProperty2<Any?, T> = value(initial).observable()

fun <T> ReadWriteProperty2<Any?, T>.observable() = object : ReadWriteProperty2<Any?, T> {
    private val onchange = EmittableEvent()
    private val thread: Thread = Thread.currentThread()

    override var value: T
        get() {
            require(Thread.currentThread() == thread)
            callContext?.calledEvents?.add(onchange)
            return this@observable.value
        }
        set(value) {
            require(Thread.currentThread() == thread)
            this@observable.value = value
            onchange.emit()
        }

    init {
        callContext?.createdEvents?.add(onchange)
    }
}

fun <T> dontObserve(block: () -> T): T = CallContext().use(block)

fun onchange(action: () -> Unit): Event = onchange<Unit>(action).second

/**
 * call [action] and intercept all called observable values. when any of this values changed, event will be emitted.
 * Warning: after event occured, observables intercepted in action can change. So you need call onchange again
 */
fun <T> onchange(action: () -> T): Pair<T, Event> {
    val callContext = CallContext()
    val result = callContext.use(action)
    return result to object : Event {
        override fun subscribe(action: () -> Unit): Disposable {
            val disposables = Disposables()
            callContext.dependencies.forEach {
                disposables += it.subscribe(action)
            }
            return disposables
        }
    }
}

fun subscribeOnchange(context: CoroutineContext, action: suspend CoroutineScope.() -> Unit, onchange: () -> Unit): Pair<Job, Disposable> {
    val subscription = Disposables()
    val callContext = CallContext()
    val job = callContext.useLaunch(context, action, afterBlock = {
        callContext.dependencies.forEach {
            subscription += it.subscribe(onchange)
        }
        callContext.calledEvents.clear()
    })
    return job to subscription
}