package com.dmi.util.concurrent

import java.util.concurrent.Future

fun checkThreadInterrupted() {
    if (Thread.currentThread().isInterrupted)
        throw InterruptedException()
}

fun waitTask(future: Future<*>) = future.get()