package com.dmi.perfectreader.fragment.book

import android.view.Choreographer
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.SlidePagesAnimation
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.graphic.SizeF
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.PublishSubject
import java.util.*

class AnimatedBook(size: SizeF, private val staticBook: StaticBook) {
    companion object {
        private val SINGLE_SLIDE_SECONDS = 0.4F
        val MAX_LOADED_PAGES = 3
    }

    val onIsAnimatingChanged = PublishSubject<Boolean>()

    val location: Location get() = staticBook.location
    var isAnimating: Boolean by rxObservable(false, onIsAnimatingChanged)
        private set
    val loadedPages = LinkedHashSet<Page>()
    val visibleSlides = ArrayList<Slide>()
    val onNewFrame = PublishSubject<Unit>()
    val onPagesChanged = staticBook.onPagesChanged

    private val animation = SlidePagesAnimation(size.width, SINGLE_SLIDE_SECONDS)
    private val frameMutex = Object()

    init {
        staticBook.onPagesChanged.subscribe {
            scheduleFrameUpdate()
        }
    }

    fun destroy() {
        staticBook.destroy()
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    fun reformat() {
        staticBook.reformat()
        scheduleFrameUpdate()
    }

    fun goLocation(location: Location) {
        staticBook.goLocation(location)
        animation.goPage()
        isAnimating = animation.isAnimating
        scheduleFrameUpdate()
    }

    fun goNextPage() {
        if (canGoNextPage()) {
            staticBook.goNextPage()
            animation.goNextPage()
            isAnimating = animation.isAnimating
            scheduleFrameUpdate()
            checkNextPageIsValid()
        }
    }

    fun goPreviousPage() {
        if (canGoPreviousPage()) {
            staticBook.goPreviousPage()
            animation.goPreviousPage()
            isAnimating = animation.isAnimating
            scheduleFrameUpdate()
            checkNextPageIsValid()
        }
    }

    private fun canGoNextPage(): Boolean {
        val slideNotTooFar = animation.hasSlides && animation.firstSlideIndex >= -Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && staticBook.canGoNextPage()
    }

    private fun canGoPreviousPage(): Boolean {
        val slideNotTooFar = animation.hasSlides && animation.lastSlideIndex <= Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && staticBook.canGoPreviousPage()
    }

    fun pageAt(relativeIndex: Int) = staticBook.pageAt(relativeIndex)

    private var updateScheduled = false
    private val frameCallback = Choreographer.FrameCallback {
        updateScheduled = false
        updateFrame()
    }

    /**
     * Может вызываться из другого потока
     */
    fun takeFrame(frame: BookFrame) = synchronized(frameMutex) {
        frame.loadedPages.retainAll(loadedPages)
        frame.loadedPages.addAll(loadedPages)
        frame.visibleSlides.clear()
        frame.visibleSlides.addAll(visibleSlides)
    }

    private fun scheduleFrameUpdate() {
        if (!updateScheduled) {
            updateScheduled = true
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }

    private fun updateFrame() = synchronized(frameMutex) {
        animation.update()
        isAnimating = animation.isAnimating
        updatePagesAndSlides()
        checkNextPageIsValid()

        onNewFrame.onNext(Unit)

        if (animation.isAnimating)
            scheduleFrameUpdate()
    }

    private fun checkNextPageIsValid() {
        if (!animation.isAnimating)
            staticBook.checkNextPageIsValid()
    }

    private fun updatePagesAndSlides() {
        loadedPages.clear()
        visibleSlides.clear()

        animation.slides.forEach { slide ->
            val page = addPage(slide.relativeIndex)
            visibleSlides.add(Slide(page, slide.offsetX))
        }

        if (animation.hasSlides) {
            if (!animation.isAnimating || animation.isGoingNext) {
                addPage(animation.lastSlideIndex + 1)
                addPage(animation.firstSlideIndex - 1)
            } else {
                addPage(animation.firstSlideIndex - 1)
                addPage(animation.lastSlideIndex + 1)
            }
        }
    }

    private fun addPage(relativeIndex: Int): Page? {
        if (loadedPages.size < MAX_LOADED_PAGES && Math.abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) {
            val page = staticBook.pageAt(relativeIndex)
            if (page != null) {
                loadedPages.add(page)
                return page
            }
        }
        return null
    }

    class Slide(val page: Page?, val offsetX: Float)
}