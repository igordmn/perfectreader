package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas
import com.dmi.perfectreader.BuildConfig
import com.dmi.perfectreader.fragment.book.page.RefreshScheduler.BitmapBuffer
import com.dmi.util.graphic.Size
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import rx.Subscription
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class)
@Suppress("IllegalIdentifier")
class RefreshSchedulerTest {
    @Test(timeout = 3000)
    fun `single refresh`() {
        // given
        val statistics = TestStatistics()
        val scheduler = scheduler()
        val refreshable = TestRefreshable(statistics)

        // when
        scheduler.schedule(refreshable)

        while (!refreshable.refreshed) {
            sleep(5)
            scheduler.refresh()
        }

        // then
        refreshable.paintOnce shouldEqual true
        refreshable.refreshOnce shouldEqual true
        refreshable.painted shouldEqual true
        refreshable.refreshed shouldEqual true
    }

    @Test(timeout = 3000)
    fun `multiple refresh`() {
        // given
        val statistics = TestStatistics()
        val scheduler = scheduler()
        val refreshables = Array(10) { TestRefreshable(statistics) }

        // when
        refreshables.forEach { scheduler.schedule(it) }

        while (statistics.refreshCount < refreshables.size) {
            sleep(5)
            scheduler.refresh()
        }

        // then
        refreshables.forEach {
            it.paintOnce shouldEqual true
            it.refreshOnce shouldEqual true
            it.painted shouldEqual true
            it.refreshed shouldEqual true
        }
    }

    @Test(timeout = 3000)
    fun `cancel refreshes`() {
        // given
        val statistics = TestStatistics()
        val scheduler = scheduler()
        val refreshables = Array(10) { TestRefreshable(statistics) }
        val subscriptions = ArrayList<Subscription>()

        // when
        refreshables.forEach {
            subscriptions.add(scheduler.schedule(it))
        }
        subscriptions[0].unsubscribe()
        subscriptions[1].unsubscribe()
        subscriptions[5].unsubscribe()
        subscriptions[9].unsubscribe()

        while (statistics.refreshCount < refreshables.size - 4) {
            sleep(5)
            scheduler.refresh()
        }

        // then
        refreshables[0].refreshed shouldEqual false
        refreshables[1].refreshed shouldEqual false
        refreshables[2].refreshed shouldEqual true
        refreshables[3].refreshed shouldEqual true
        refreshables[4].refreshed shouldEqual true
        refreshables[5].refreshed shouldEqual false
        refreshables[6].refreshed shouldEqual true
        refreshables[7].refreshed shouldEqual true
        refreshables[8].refreshed shouldEqual true
        refreshables[9].refreshed shouldEqual false
    }

    @Test(timeout = 3000)
    fun `notify only when previous refreshed`() {
        // given
        val needRefreshCount = AtomicInteger(0)
        val statistics = TestStatistics()
        val scheduler = scheduler()
        val refreshables = Array(10) { TestRefreshable(statistics) }
        val subscriptions = ArrayList<Subscription>()

        // when
        scheduler.onNeedRefresh.subscribe {
            val refreshCountExpected = needRefreshCount.get()
            refreshCountExpected shouldEqual statistics.refreshCount
            for (i in 0..refreshCountExpected - 1) {
                refreshables[i].refreshed shouldEqual true
            }
            for (i in refreshCountExpected..refreshables.size - 1) {
                refreshables[i].refreshed shouldEqual false
            }

            scheduler.refresh()

            refreshables[refreshCountExpected].refreshed shouldEqual true
            needRefreshCount.incrementAndGet()
        }
        refreshables.forEach {
            subscriptions.add(scheduler.schedule(it))
        }

        while (statistics.refreshCount < refreshables.size) {
            sleep(5)
        }

        // then
        needRefreshCount.get() shouldEqual statistics.refreshCount
    }

    @Test(timeout = 3000)
    fun `don't paint next until refresh`() {
        // given
        val statistics = TestStatistics()
        val scheduler = scheduler()
        val refreshable1 = TestRefreshable(statistics)
        val refreshable2 = TestRefreshable(statistics)

        // when
        scheduler.schedule(refreshable1)
        scheduler.schedule(refreshable2)
        Thread.sleep(100)

        // then
        refreshable1.painted shouldEqual true
        refreshable1.refreshed shouldEqual false
        refreshable2.painted shouldEqual false
        refreshable2.refreshed shouldEqual false

        // when
        scheduler.refresh()
        Thread.sleep(100)

        // then
        refreshable1.painted shouldEqual true
        refreshable1.refreshed shouldEqual true
        refreshable2.painted shouldEqual true
        refreshable2.refreshed shouldEqual false

        // when
        scheduler.refresh()
        Thread.sleep(100)

        // then
        refreshable1.painted shouldEqual true
        refreshable1.refreshed shouldEqual true
        refreshable2.painted shouldEqual true
        refreshable2.refreshed shouldEqual true
    }

    fun scheduler() = RefreshScheduler(BitmapBuffer(Size(100, 100), 2F))

    class TestStatistics {
        @Volatile var refreshCount = 0
    }

    class TestRefreshable(val statistics: TestStatistics) : RefreshScheduler.Refreshable {
        @Volatile var refreshed = false
            private set
        @Volatile var painted = false
            private set
        @Volatile var paintOnce = true
        @Volatile var refreshOnce = true

        override fun paint(canvas: Canvas) {
            if (painted)
                paintOnce = false

            painted = true
            sleep(10)
        }

        override fun refreshBy(bitmap: Bitmap) {
            if (refreshed)
                refreshOnce = false
            refreshed = true
            statistics.refreshCount++
        }
    }
}