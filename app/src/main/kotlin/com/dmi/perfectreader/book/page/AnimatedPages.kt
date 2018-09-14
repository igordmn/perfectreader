package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.doubleRound
import com.dmi.util.lang.longFloor
import com.dmi.util.lang.modPositive
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.EmittableEvent
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.system.Display
import java.lang.Math.*

class AnimatedPages(
        val size: SizeF,
        private val pages: Pages,
        private val display: Display,
        private val speedToTurnPage: Float, // pixels per second
        private val animator: PageAnimator,
        scope: Scope = Scope()
) : Disposable by scope {
    companion object {
        fun pages(pages: LoadingPages) = object : Pages {
            override fun get(relativeIndex: Int) = pages[relativeIndex]
            override val goIndices get() = pages.goIndices
            override fun goRelative(relativeIndex: Int) = pages.goRelative(relativeIndex)
        }
    }

    private var currentIndex: Int by observable(0)
    private var animation by observable(PageAnimation(display.currentTime))
    private var isScrolling by observable(false)

    private fun movingPageToIndex(movingPage: Long): Int = (movingPage - currentIndex).toInt()
    private fun indexToMovingPage(actualPage: Int): Long = actualPage + currentIndex.toLong()

    val visible: VisiblePages by scope.cached {
        val leftPage = longFloor(animation.currentPage)
        val rightPage = leftPage + 1
        val futurePage = if (animation.targetPage - leftPage >= 0.5) rightPage + 1 else leftPage - 1
        val animationX = (animation.currentPage modPositive 1.0).toFloat()
        VisiblePages(
                left = pages[movingPageToIndex(leftPage)],
                right = pages[movingPageToIndex(rightPage)],
                future = pages[movingPageToIndex(futurePage)],
                leftProgress = -animationX,
                rightProgress = 1 - animationX
        )
    }

    val isMoving: Boolean get() = !animation.isStill || isScrolling

    private val afterAnimate = EmittableEvent()

    init {
        scope.launch {
            while (true) {
                if (!animation.isStill) {
                    val time = display.waitVSyncTime()
                    animation = animator.update(animation, time)
                    afterAnimate()
                } else {
                    afterAnimate.wait()
                }
            }
        }
    }

    fun reset() {
        animation = animation.reset()
        currentIndex = 0
    }

    fun animateRelative(relativeIndex: Int) {
        if (relativeIndex == 0) return
        animation = animator
                .update(animation, display.currentTime)
                .copy(targetPage = shiftedPage(animation.targetPage, relativeIndex))
        afterAnimate()
    }

    private fun shiftedPage(movingPage: Double, relativeIndex: Int): Double {
        val limitedDelta = limitedPageDelta(movingPage, relativeIndex.toDouble())
        return if (limitedDelta >= 0) floor(movingPage + limitedDelta) else ceil(movingPage + limitedDelta)
    }

    private fun limitedPageDelta(movingPage: Double, delta: Double): Double = when {
        delta > 0.0 -> {
            val maxMovingPage = indexToMovingPage(pages.goIndices.endInclusive)
            val maxDelta = maxMovingPage - movingPage
            min(maxDelta, delta)
        }
        delta < 0.0 -> {
            val minMovingPage = indexToMovingPage(pages.goIndices.start)
            val minDelta = minMovingPage - movingPage
            max(minDelta, delta)
        }
        else -> 0.0
    }

    fun scroll(): PageScroller = object : PageScroller {
        private var ended = false

        init {
            isScrolling = true
        }

        override fun scroll(delta: PositionF) {
            require(!ended)
            val pageDeltaX = delta.x / size.width.toDouble()
            val limitedDeltaX = if (pageDeltaX >= 0) {
                limitedPageDelta(max(animation.currentPage, animation.targetPage), pageDeltaX)
            } else {
                limitedPageDelta(min(animation.currentPage, animation.targetPage), pageDeltaX)
            }

            animation = animator
                    .update(animation, display.currentTime)
                    .copy(
                            targetPage = animation.targetPage + limitedDeltaX,
                            currentPage = animation.currentPage + limitedDeltaX
                    )
            afterAnimate()
        }

        override fun end(velocity: PositionF) = if (isEnoughSpeed(velocity)) {
            require(!ended)
            ended = true
            isScrolling = false
            val relativeIndex = if (isScrollRight(velocity)) 1 else -1
            animation = animator
                    .update(animation, display.currentTime)
                    .copy(
                            targetPage = shiftedPage(animation.targetPage, relativeIndex),
                            velocity = animation.velocity + velocity.x / size.width
                    )
            afterAnimate()
        } else {
            cancel()
        }

        private fun isEnoughSpeed(velocity: PositionF) = abs(velocity.x) >= speedToTurnPage
        private fun isScrollRight(velocity: PositionF) = velocity.x >= 0

        override fun cancel() {
            require(!ended)
            ended = true
            isScrolling = false
            animation = animator
                    .update(animation, display.currentTime)
                    .copy(targetPage = doubleRound(animation.targetPage))
            afterAnimate()
        }
    }

    private fun afterAnimate() {
        synchronizePages()
        afterAnimate.emit()
    }

    private fun synchronizePages() {
        val diff = (animation.currentPage - currentIndex).toInt()
        pages.goRelative(diff)
        currentIndex += diff
    }

    interface Pages {
        operator fun get(relativeIndex: Int): Page?
        val goIndices: IntRange
        fun goRelative(relativeIndex: Int)
    }
}