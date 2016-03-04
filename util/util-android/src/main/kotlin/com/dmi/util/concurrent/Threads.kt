package com.dmi.util.concurrent

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

object Threads {
    private val uiHandler = Handler(Looper.getMainLooper())

    fun postUITask(task: () -> Unit): Future<*> {
        val futureTask = object : FutureTask<Void>(task, null) {
            override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                uiHandler.removeCallbacks(task)
                return super.cancel(mayInterruptIfRunning)
            }
        }
        if (!uiHandler.post(futureTask)) {
            futureTask.cancel(true)
        }
        return futureTask
    }
}