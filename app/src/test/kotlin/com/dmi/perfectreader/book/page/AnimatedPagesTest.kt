package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.test.shouldBe
import com.dmi.util.coroutine.initThreadContext
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.intRound
import com.dmi.util.scope.EmittableEvent
import com.dmi.util.system.Display
import com.dmi.util.system.Nanos
import com.dmi.util.system.toSeconds
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.yield
import org.junit.Test

class AnimatedPagesTest {
    private val context = newSingleThreadContext("test")

    init {
        runBlocking(context) {
            initThreadContext(context)
        }
    }

    class TestPages(vararg ranges: ClosedRange<Int>) : AnimatedPages.Pages {
        private val pages = ranges.map { testPage(it.start.toDouble()..it.endInclusive.toDouble()) }
        var index = 0

        override fun get(relativeIndex: Int): Page? {
            return if (index + relativeIndex in 0 until pages.size) pages[index + relativeIndex] else null
        }

        override val goIndices: IntRange get() = -index until pages.size - index

        override fun goRelative(relativeIndex: Int) {
            require(relativeIndex in goIndices)
            index += relativeIndex
        }
    }

    class TestDisplay : Display {
        override var currentTime: Nanos = 0

        private val onvsync = EmittableEvent()

        suspend fun vsync() {
            yield()
            currentTime = (1 + currentTime / 100) * 100
            onvsync.emit()
            yield()
        }

        /**
         * every 100 nanoseconds
         */
        override suspend fun waitVSyncTime(): Nanos {
            onvsync.wait()
            return currentTime
        }
    }

    data class VisiblePagesTest(
            val leftRange: ClosedRange<Int>?, val rightRange: ClosedRange<Int>?, val futureRange: ClosedRange<Int>?,
            val leftProgress: Int, val rightProgress: Int
    )

    val LocationRange.intOffsets get() = start.offset.toInt()..endInclusive.offset.toInt()

    val VisiblePages.test
        get() = VisiblePagesTest(
                left?.range?.intOffsets, right?.range?.intOffsets, future?.range?.intOffsets,
                intRound(leftProgress * 100), intRound(rightProgress * 100)
        )

    @Test
    fun `single page`() = runBlocking(context) {
        val display = TestDisplay()
        val pages = TestPages(0..10)
        val animatedPages = AnimatedPages(
                SizeF(100F, 100F), pages, display,
                speedToTurnPage = 10F, animator = LinearPageAnimator(singlePageTime = 400)
        )

        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        animatedPages.animateRelative(-1)
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        display.vsync()
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        animatedPages.animateRelative(1)
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        display.vsync()
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        val scroller = animatedPages.scroll()
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        scroller.scroll(PositionF(10F, 10F))
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        scroller.scroll(PositionF(-20F, -20F))
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)

        scroller.end(PositionF(-20F, -20F))
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, null, null, -0, 100)
    }

    @Test
    fun `animate forward and backward`() = runBlocking(context) {
        val display = TestDisplay()
        val pages = TestPages(0..10, 10..20, 20..30, 30..40)
        val animatedPages = AnimatedPages(
                SizeF(100F, 100F), pages, display,
                speedToTurnPage = 10F, animator = LinearPageAnimator(singlePageTime = 400)
        )

        display.currentTime shouldBe 0L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        animatedPages.animateRelative(-1)
        display.currentTime shouldBe 0L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        animatedPages.animateRelative(1)
        display.currentTime shouldBe 0L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -0, 100)

        display.vsync()
        display.currentTime shouldBe 100L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -25, 75)

        display.vsync()
        display.currentTime shouldBe 200L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -50, 50)

        display.vsync()
        display.currentTime shouldBe 300L
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -75, 25)

        display.vsync()
        display.currentTime shouldBe 400L
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -0, 100)

        animatedPages.animateRelative(10)
        display.currentTime shouldBe 400L
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -0, 100)

        display.vsync()
        display.currentTime shouldBe 500L
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -25, 75)

        display.vsync()
        display.vsync()
        display.vsync()
        display.currentTime shouldBe 800L
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -0, 100)

        display.vsync()
        display.currentTime shouldBe 900L
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -25, 75)

        display.vsync()
        display.vsync()
        display.vsync()
        pages.index shouldBe 3
        display.currentTime shouldBe 1200L
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        animatedPages.animateRelative(1)
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        display.vsync()
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        animatedPages.animateRelative(-1)
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        display.vsync()
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -75, 25)

        animatedPages.animateRelative(1)
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -75, 25)

        display.vsync()
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        animatedPages.animateRelative(-2)
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(30..40, null, 20..30, -0, 100)

        display.vsync()
        display.vsync()
        display.vsync()
        pages.index shouldBe 3
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -25, 75)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -0, 100)

        display.vsync()
        display.vsync()
        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -25, 75)

        animatedPages.animateRelative(1)
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -25, 75)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -50, 50)

        animatedPages.animateRelative(-1)
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -50, 50)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -25, 75)

        display.vsync()
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -0, 100)

        animatedPages.animateRelative(-1)
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -0, 100)

        display.vsync()
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -75, 25)

        animatedPages.animateRelative(-1)
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -75, 25)

        display.vsync()
        display.vsync()
        display.vsync()
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)
    }

    @Test
    fun scrolling() = runBlocking(context) {
        val display = TestDisplay()
        val pages = TestPages(0..10, 10..20, 20..30, 30..40)
        val size = SizeF(100F, 100F)
        fun speed(singlePageTime: Nanos) = (size.width / singlePageTime.toSeconds()).toFloat()
        val animatedPages = AnimatedPages(
                size, pages, display,
                speedToTurnPage = speed(800), animator = LinearPageAnimator(singlePageTime = 400)
        )

        var scroller = animatedPages.scroll()
        animatedPages.isMoving shouldBe true

        scroller.scroll(PositionF(-10F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        display.vsync()
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        scroller.scroll(PositionF(10F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -10, 90)

        scroller.scroll(PositionF(20F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -30, 70)

        scroller.scroll(PositionF(30F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -60, 40)

        display.vsync()
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -60, 40)

        scroller.scroll(PositionF(-100F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        scroller.scroll(PositionF(60F, -10F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -60, 40)

        scroller.end(PositionF(-speed(200), 0F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -60, 40)

        display.vsync()
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -10, 90)

        display.vsync()
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, null, -0, 100)

        animatedPages.scroll().end(PositionF(speed(1), 0F))
        pages.index shouldBe 0
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(0..10, 10..20, 20..30, -0, 100)

        display.vsync()
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 0..10, -0, 100)

        scroller = animatedPages.scroll()
        scroller.scroll(PositionF(60F, 0F))
        scroller.end(PositionF(-speed(801), 0F))
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -60, 40)

        display.vsync()
        pages.index shouldBe 1
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(10..20, 20..30, 30..40, -85, 15)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -0, 100)

        animatedPages.animateRelative(1)
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -0, 100)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -25, 75)

        scroller = animatedPages.scroll()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -25, 75)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -50, 50)

        scroller.cancel()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -50, 50)

        display.vsync()
        pages.index shouldBe 2
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -75, 25)

        scroller = animatedPages.scroll()
        scroller.scroll(PositionF(10F, 10F))
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -75, 25)

        scroller.scroll(PositionF(-10F, 10F))
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, null, -65, 35)

        scroller.end(PositionF(-speed(400), 0F))
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -65, 35)

        display.vsync()
        animatedPages.isMoving shouldBe true
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -40, 60)

        display.vsync()
        display.vsync()
        animatedPages.isMoving shouldBe false
        animatedPages.visible.test shouldBe VisiblePagesTest(20..30, 30..40, 10..20, -0, 100)
    }
}