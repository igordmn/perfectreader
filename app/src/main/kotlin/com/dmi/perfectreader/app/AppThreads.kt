package com.dmi.perfectreader.app

import com.dmi.util.concurrent.Threads
import java.util.concurrent.Executors
import java.util.concurrent.Future

object AppThreads {
    private val ioExecutor = Executors.newSingleThreadExecutor()

    fun postUITask(task: () -> Unit): Future<*> {
        return Threads.postUITask(task)
    }

    fun postIOTask(task: () -> Unit): Future<*> {
        return ioExecutor.submit(task)
    }
}
