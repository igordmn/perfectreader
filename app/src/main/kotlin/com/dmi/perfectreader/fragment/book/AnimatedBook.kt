package com.dmi.perfectreader.fragment.book

import android.view.Choreographer
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.SlidePagesAnimation
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.PublishSubject
import java.util.*

class AnimatedBook(
        val size: SizeF,
        dip2px: (Float) -> Float,
        val staticBook: StaticBook) {
    companion object {
        private val SINGLE_SLIDE_SECONDS = 0.4F
        val MAX_LOADED_PAGES = 3
    }

    private val scrollSpeedToTurnPage = dip2px(50F)
    private val scrollDistanceToTurnPage = dip2px(100F)

    val onIsAnimatingChanged = PublishSubject<Boolean>()

    val location: Location get() = staticBook.location
    val locationConverter: LocationConverter get() = staticBook.locationConverter
    var isAnimating: Boolean by rxObservable(false, onIsAnimatingChanged)
        private set
    val onNewFrame = PublishSubject<Unit>()
    val onPagesChanged = staticBook.onPagesChanged

    private val animation = SlidePagesAnimation(size.width, SINGLE_SLIDE_SECONDS)
    private val frameMutex = Object()

    init {
        staticBook.onPagesChanged.subscribe {
            scheduleFrameUpdate()
        }
    }

    fun destroy() = synchronized(frameMutex) {
        staticBook.destroy()
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    fun reformat() = synchronized(frameMutex) {
        staticBook.reformat()
        scheduleFrameUpdate()
    }

    fun goLocation(location: Location) = synchronized(frameMutex) {
        staticBook.goLocation(location)
        animation.goPage()
        isAnimating = animation.isAnimating
        scheduleFrameUpdate()
    }

    fun scroll(startPosition: PositionF) = object : Scroller {
        override fun scroll(delta: PositionF) {

        }

        override fun end(velocity: PositionF) {

        }

        override fun cancel() {

        }
    }

    fun goNextPage() = synchronized(frameMutex) {
        if (canGoNextPage()) {
            staticBook.goNextPage()
            animation.goNextPage()
            isAnimating = animation.isAnimating
            checkNextPageIsValid()
            scheduleFrameUpdate()
        }
    }

    fun goPreviousPage() = synchronized(frameMutex) {
        if (canGoPreviousPage()) {
            staticBook.goPreviousPage()
            animation.goPreviousPage()
            isAnimating = animation.isAnimating
            checkNextPageIsValid()
            scheduleFrameUpdate()
        }
    }

    private fun canGoNextPage(): Boolean = synchronized(frameMutex) {
        val slideNotTooFar = animation.hasSlides && animation.firstSlideIndex >= -Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && staticBook.canGoNextPage()
    }

    private fun canGoPreviousPage(): Boolean = synchronized(frameMutex) {
        val slideNotTooFar = animation.hasSlides && animation.lastSlideIndex <= Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && staticBook.canGoPreviousPage()
    }

    fun pageAt(relativeIndex: Int) = staticBook.pageAt(relativeIndex)

    /**
     * Может вызываться из другого потока
     */
    fun takeFrame(frame: BookFrame) = synchronized(frameMutex) {
        animation.update()

        frame.loadedPages.clear()
        frame.visibleSlides.clear()

        animation.slides.forEach { slide ->
            val page = addLoadedPage(slide.relativeIndex, frame.loadedPages)
            frame.visibleSlides.add(Slide(page, slide.offsetX))
        }

        if (animation.hasSlides) {
            if (!animation.isAnimating || animation.isGoingNext) {
                addLoadedPage(animation.lastSlideIndex + 1, frame.loadedPages)
                addLoadedPage(animation.firstSlideIndex - 1, frame.loadedPages)
            } else {
                addLoadedPage(animation.firstSlideIndex - 1, frame.loadedPages)
                addLoadedPage(animation.lastSlideIndex + 1, frame.loadedPages)
            }
        }
    }

    private fun addLoadedPage(relativeIndex: Int, loadedPages: LinkedHashSet<Page>): Page? {
        if (loadedPages.size < MAX_LOADED_PAGES && Math.abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) {
            val page = staticBook.pageAt(relativeIndex)
            if (page != null) {
                loadedPages.add(page)
                return page
            }
        }
        return null
    }

    private var frameUpdateScheduled = false
    private val frameCallback = Choreographer.FrameCallback {
        frameUpdateScheduled = false
        updateFrame()
    }

    private fun scheduleFrameUpdate() {
        if (!frameUpdateScheduled) {
            frameUpdateScheduled = true
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }

    private fun updateFrame() = synchronized(frameMutex) {
        animation.update()
        isAnimating = animation.isAnimating
        checkNextPageIsValid()

        onNewFrame.onNext(Unit)

        if (animation.isAnimating)
            scheduleFrameUpdate()
    }

    private fun checkNextPageIsValid() = synchronized(frameMutex) {
        if (!animation.isAnimating)
            staticBook.checkNextPageIsValid()
    }

    class Slide(val page: Page?, val offsetX: Float)

    interface Scroller {
        fun scroll(delta: PositionF)
        fun end(velocity: PositionF)
        fun cancel()
    }
}