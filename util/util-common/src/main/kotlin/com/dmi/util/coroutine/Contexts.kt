package com.dmi.util.coroutine

import com.dmi.util.lang.threadLocal
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlin.coroutines.experimental.CoroutineContext

val IOPool = newSingleThreadContext("IO")

var threadContext: CoroutineContext by threadLocal()
    private set
var threadContextInit: Boolean by threadLocal(false)
    private set

fun initThreadContext(threadContext: CoroutineContext) {
    require(!threadContextInit)
    com.dmi.util.coroutine.threadContext = threadContext
    threadContextInit = true
}