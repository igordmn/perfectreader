package com.dmi.util.concurrent

import org.amshove.kluent.shouldEqual
import org.junit.Test
import rx.Subscription
import rx.schedulers.Schedulers
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ResourceQueueProcessorTest {
    @Test(timeout = 3000)
    fun `single process`() {
        // given
        val statistics = TestStatistics()
        val processor = processor()
        val task = TestTask(statistics)

        // when
        processor.scheduleProcess(task)

        while (!task.completed) {
            sleep(5)
            processor.checkComplete()
        }

        // then
        task.processedOnce shouldEqual true
        task.completedOnce shouldEqual true
        task.processed shouldEqual true
        task.completed shouldEqual true
    }

    @Test(timeout = 3000)
    fun `multiple process`() {
        // given
        val statistics = TestStatistics()
        val processor = processor()
        val tasks = Array(10) { TestTask(statistics) }

        // when
        tasks.forEach { processor.scheduleProcess(it) }

        while (statistics.completeCount < tasks.size) {
            sleep(5)
            processor.checkComplete()
        }

        // then
        tasks.forEach {
            it.processedOnce shouldEqual true
            it.completedOnce shouldEqual true
            it.processed shouldEqual true
            it.completed shouldEqual true
        }
    }

    @Test(timeout = 3000)
    fun `cancel processes`() {
        // given
        val statistics = TestStatistics()
        val processor = processor()
        val tasks = Array(10) { TestTask(statistics) }
        val subscriptions = ArrayList<Subscription>()

        // when
        tasks.forEach {
            subscriptions.add(processor.scheduleProcess(it))
        }
        subscriptions[0].unsubscribe()
        subscriptions[1].unsubscribe()
        subscriptions[5].unsubscribe()
        subscriptions[9].unsubscribe()

        while (statistics.completeCount < tasks.size - 4) {
            sleep(5)
            processor.checkComplete()
        }

        // then
        tasks[0].completed shouldEqual false
        tasks[1].completed shouldEqual false
        tasks[2].completed shouldEqual true
        tasks[3].completed shouldEqual true
        tasks[4].completed shouldEqual true
        tasks[5].completed shouldEqual false
        tasks[6].completed shouldEqual true
        tasks[7].completed shouldEqual true
        tasks[8].completed shouldEqual true
        tasks[9].completed shouldEqual false
    }

    @Test(timeout = 3000)
    fun `notify only when previous completed`() {
        // given
        val needRefreshCount = AtomicInteger(0)
        val statistics = TestStatistics()
        val processor = processor()
        val tasks = Array(10) { TestTask(statistics) }
        val subscriptions = ArrayList<Subscription>()

        // when
        processor.onNeedCheck.subscribe {
            val completeCountExpected = needRefreshCount.get()
            completeCountExpected shouldEqual statistics.completeCount
            for (i in 0..completeCountExpected - 1) {
                tasks[i].completed shouldEqual true
            }
            for (i in completeCountExpected..tasks.size - 1) {
                tasks[i].completed shouldEqual false
            }

            processor.checkComplete()

            tasks[completeCountExpected].completed shouldEqual true
            needRefreshCount.incrementAndGet()
        }
        tasks.forEach {
            subscriptions.add(processor.scheduleProcess(it))
        }

        while (statistics.completeCount < tasks.size) {
            sleep(5)
        }

        // then
        needRefreshCount.get() shouldEqual statistics.completeCount
    }

    @Test(timeout = 3000)
    fun `don't process next until previous is completed`() {
        // given
        val statistics = TestStatistics()
        val processor = processor()
        val task1 = TestTask(statistics)
        val task2 = TestTask(statistics)

        // when
        processor.scheduleProcess(task1)
        processor.scheduleProcess(task2)
        sleep(100)

        // then
        task1.processed shouldEqual true
        task1.completed shouldEqual false
        task2.processed shouldEqual false
        task2.completed shouldEqual false

        // when
        processor.checkComplete()
        sleep(100)

        // then
        task1.processed shouldEqual true
        task1.completed shouldEqual true
        task2.processed shouldEqual true
        task2.completed shouldEqual false

        // when
        processor.checkComplete()
        sleep(100)

        // then
        task1.processed shouldEqual true
        task1.completed shouldEqual true
        task2.processed shouldEqual true
        task2.completed shouldEqual true
    }

    fun processor() = ResourceQueueProcessor(Unit, Schedulers.newThread())
    fun ResourceQueueProcessor<Unit>.scheduleProcess(task: TestTask) = scheduleProcess({ task.process() }, { task.afterProcess() })

    class TestStatistics {
        @Volatile var completeCount = 0
    }

    class TestTask(val statistics: TestStatistics) {
        @Volatile var completed = false
            private set
        @Volatile var processed = false
            private set
        @Volatile var processedOnce = true
        @Volatile var completedOnce = true

        fun process() {
            if (processed)
                processedOnce = false

            processed = true
            sleep(10)
        }

        fun afterProcess() {
            if (completed)
                completedOnce = false
            completed = true
            statistics.completeCount++
        }
    }
}