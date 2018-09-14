package com.dmi.util.coroutine

import com.dmi.util.lang.threadLocal
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