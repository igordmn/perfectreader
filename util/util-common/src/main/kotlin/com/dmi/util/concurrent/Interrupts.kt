package com.dmi.util.concurrent

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

object Interrupts {
    fun checkThreadInterrupted() {
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException()
        }
    }

    fun waitTask(future: Future<*>) {
        try {
            future.get()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }
    }
}
