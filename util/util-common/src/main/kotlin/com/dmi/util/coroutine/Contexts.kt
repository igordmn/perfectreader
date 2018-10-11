package com.dmi.util.coroutine

import com.dmi.util.lang.threadLocal
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

var threadContext: CoroutineContext by threadLocal()
    private set
var threadContextInit: Boolean by threadLocal(false)
    private set

fun initThreadContext(threadContext: CoroutineContext) {
    require(!threadContextInit)
    com.dmi.util.coroutine.threadContext = threadContext
    threadContextInit = true
}

fun CoroutineContext.wrap(wrapBlock: (() -> Unit) -> Unit): CoroutineContext {
    class WrapContinuation<T>(val cont: Continuation<T>) : Continuation<T> by cont {
        override fun resumeWith(result: Result<T>) = wrapBlock {
            cont.resumeWith(result)
        }
    }

    return this + object : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
        override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
            val wrapped = WrapContinuation(continuation)
            return this@wrap[ContinuationInterceptor]?.interceptContinuation(wrapped) ?: wrapped
        }
    }
}