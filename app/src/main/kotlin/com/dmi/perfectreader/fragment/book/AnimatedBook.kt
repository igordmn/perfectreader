package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.animation.PageScroller
import com.dmi.perfectreader.fragment.book.animation.PagesAnimator
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.android.update.FrameUpdater
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.doubleRound
import com.dmi.util.lang.longFloor
import com.dmi.util.lang.modPositive
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.PublishSubject
import rx.subjects.BehaviorSubject
import java.lang.Math.*
import java.lang.System.nanoTime
import java.net.URI

class AnimatedBook(
        val size: SizeF,
        private val staticBook: StaticBook,
        animatorConfig: PagesAnimator.Config,
        private val speedToTurnPage: Float
) {

    val onNewFrame = PublishSubject<Unit>()
    val onPagesChanged = staticBook.onPagesChanged
    val onIsAnimatingChanged = PublishSubject<Boolean>()

    val locationObservable: BehaviorSubject<Location> get() = staticBook.locationObservable
    val location: Location get() = staticBook.location
    val locationConverter: LocationConverter get() = staticBook.locationConverter
    var isAnimating: Boolean by rxObservable(false, onIsAnimatingChanged)
        private set

    var pages: AnimatedPages = AnimatedPages.NONE
        private set

    val animationUri = URI("assets:///resources/animations/curl.xml")
    private var animator = PagesAnimator.zero(animatorConfig, nanoTime())
    private var isScrolling = false
    private var staticAnimatorIndex: Long = 0  // положение текущей страницы staticBook в пространстве индексов аниматора

    init {
        staticBook.onPagesChanged.subscribe {
            frameUpdater.scheduleUpdate()
        }
    }

    fun destroy() {
        staticBook.destroy()
        frameUpdater.cancel()
    }

    fun reformat() {
        staticBook.reformat()
        afterAnimate()
    }

    fun goLocation(location: Location) {
        if (location == this.location) return

        animator = animator.reset()
        staticBook.goLocation(location)
        staticAnimatorIndex = 0
        afterAnimate()
    }

    fun goPage(relativeIndex: Int) {
        if (relativeIndex == 0) return

        animator = animator.reset()
        staticBook.goPage(relativeIndex)
        staticAnimatorIndex = 0
        afterAnimate()
    }

    fun scroll(): PageScroller = object : PageScroller {
        init {
            isScrolling = true
        }

        override fun scroll(delta: PositionF) {
            val pageDeltaX = (-delta.x / size.width).toDouble()
            val limitedDeltaX = if (pageDeltaX >= 0) {
                limitedPageDelta(max(animator.currentPage, animator.targetPage), pageDeltaX)
            } else {
                limitedPageDelta(min(animator.currentPage, animator.targetPage), pageDeltaX)
            }

            animator = animator
                    .update(nanoTime())
                    .targetPage(animator.targetPage + limitedDeltaX)
                    .currentPage(animator.currentPage + limitedDeltaX)
            afterAnimate()
        }

        override fun end(velocity: PositionF) = if (isEnoughSpeed(velocity)) {
            isScrolling = false
            val relativeIndex = if (isScrollLeft(velocity)) 1 else -1
            animator = animator
                    .update(nanoTime())
                    .targetPage(shiftedPage(animator.targetPage, relativeIndex))
                    .velocity(animator.velocity - velocity.x / size.width)
            afterAnimate()
        } else {
            cancel()
        }

        private fun isEnoughSpeed(velocity: PositionF) = abs(velocity.x) >= speedToTurnPage
        private fun isScrollLeft(velocity: PositionF) = velocity.x <= 0

        override fun cancel() {
            animator = animator
                    .update(nanoTime())
                    .targetPage(doubleRound(animator.targetPage))
            isScrolling = false
            afterAnimate()
        }
    }

    fun movePage(relativeIndex: Int) {
        if (relativeIndex == 0) return

        animator = animator
                .update(nanoTime())
                .targetPage(shiftedPage(animator.targetPage, relativeIndex))
        afterAnimate()
    }

    private fun shiftedPage(animatorPage: Double, relativeIndex: Int): Double {
        val limitedDelta = limitedPageDelta(animatorPage, relativeIndex.toDouble())
        return if (limitedDelta >= 0) floor(animatorPage + limitedDelta) else ceil(animatorPage + limitedDelta)
    }

    private fun limitedPageDelta(animatorPage: Double, delta: Double): Double = when {
        delta > 0.0 -> {
            val maxAnimatorIndex = staticToAnimatorIndex(staticBook.maxGoRelativeIndex)
            val maxDelta = maxAnimatorIndex - animatorPage
            min(maxDelta, delta)
        }
        delta < 0.0 -> {
            val minAnimatorIndex = staticToAnimatorIndex(staticBook.minGoRelativeIndex)
            val minDelta = minAnimatorIndex - animatorPage
            max(minDelta, delta)
        }
        else -> 0.0
    }

    fun pageAt(relativeIndex: Int) = staticBook.pageAt(relativeIndex)

    private val frameUpdater: FrameUpdater = object : FrameUpdater() {
        override fun update() {
            animator = animator.update(nanoTime())
            afterAnimate()
        }
    }

    private fun afterAnimate() {
        isAnimating = animator.willChange || isScrolling

        synchronizeStaticBook()
        pages = snapshotPages()

        if (animator.willChange)
            frameUpdater.scheduleUpdate()

        onNewFrame.onNext(Unit)
    }

    private fun synchronizeStaticBook() {
        val diff = (animator.currentPage - staticAnimatorIndex).toInt()
        staticBook.goPage(diff)
        staticAnimatorIndex += diff
    }

    private fun animatorToStaticIndex(animatorIndex: Long): Int = (animatorIndex - staticAnimatorIndex).toInt()
    private fun staticToAnimatorIndex(staticIndex: Int): Long = (staticIndex + staticAnimatorIndex).toLong()

    private fun snapshotPages(): AnimatedPages {
        val leftIndex = longFloor(animator.currentPage)
        val rightIndex = leftIndex + 1
        val futureIndex = if (animator.targetPage - leftIndex >= 0.5) rightIndex + 1 else leftIndex - 1

        val leftPage = staticBook.pageAt(animatorToStaticIndex(leftIndex))
        val rightPage = staticBook.pageAt(animatorToStaticIndex(rightIndex))
        val futurePage = staticBook.pageAt(animatorToStaticIndex(futureIndex))

        val animationX = (animator.currentPage modPositive 1.0).toFloat()
        val leftProgress = -animationX
        val rightProgress = 1 - animationX

        return AnimatedPages(leftPage, rightPage, futurePage, leftProgress, rightProgress)
    }

    class AnimatedPages(val left: Page?, val right: Page?, val future: Page?, val leftProgress: Float, val rightProgress: Float) {
        companion object {
            val MAX_PAGES = 3
            val NONE = AnimatedPages(null, null, null, 0F, 1F)
        }
    }
}