package com.dmi.util.concurrent

import com.dmi.util.collection.SinglePool
import com.dmi.util.ext.LambdaObservable
import rx.Scheduler
import rx.Subscription
import rx.lang.kotlin.PublishSubject
import java.util.*

class ResourceQueueProcessor<T>(resource: T, private val scheduler: Scheduler) {
    val onNeedCheck = PublishSubject<Unit>()

    private val pool = SinglePool { resource }
    private val queue: Queue<ProcessTask> = LinkedList()
    private var currentProcess: Process? = null

    fun destroy() {
        queue.clear()
        currentProcess?.cancel()
        currentProcess = null
    }

    fun scheduleProcess(process: (T) -> Unit, afterProcess: (T) -> Unit): Subscription {
        val task = ProcessTask(process, afterProcess)

        queue.offer(task)

        if (currentProcess == null)
            startNext()

        return object : Subscription {
            override fun isUnsubscribed() = throw UnsupportedOperationException()

            override fun unsubscribe() {
                val current = currentProcess
                if (current != null && task == current.task) {
                    current.cancel()
                } else {
                    queue.remove(task)
                }
            }
        }
    }

    private fun startNext() {
        val task = queue.poll()
        if (task != null) {
            currentProcess = Process(task) {
                currentProcess = null
                startNext()
            }
        }
    }

    fun checkComplete() {
        currentProcess?.checkComplete()
    }

    private inner class Process(
            val task: ProcessTask,
            private val onComplete: () -> Unit
    ) {
        private @Volatile var resource: T? = null
        private var cancelled = false

        init {
            LambdaObservable {
                val resource = pool.acquire()
                task.process(resource)
                resource
            }.subscribeOn(scheduler).subscribe {
                resource = it
                onNeedCheck.onNext(Unit)
            }
        }

        fun checkComplete() {
            val resource = this.resource
            if (resource != null) {
                if (!cancelled)
                    task.afterProcess(resource)
                pool.release(resource)
                onComplete()
            }
        }

        fun cancel() {
            cancelled = true
        }
    }

    private inner class ProcessTask(val process: (T) -> Unit, val afterProcess: (T) -> Unit)
}