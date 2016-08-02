package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.SlidePagesAnimation
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.graphic.SizeF
import com.dmi.util.mainScheduler
import com.dmi.util.rx.runOn
import rx.lang.kotlin.PublishSubject
import java.util.*

class AnimatedBook(size: SizeF, private val sized: SizedBook) {
    companion object {
        private val SINGLE_SLIDE_SECONDS = 0.4F
        val MAX_LOADED_PAGES = 3
    }

    val location: Location get() = sized.location
    val pageContext: PageContext get() = sized.pageContext
    val loadedPages = LinkedHashSet<Page>()
    val visibleSlides = ArrayList<Slide>()
    val onChanged = PublishSubject<Unit>()

    private val animation = SlidePagesAnimation(size.width, SINGLE_SLIDE_SECONDS)
    private val sizedPagesSnapshot = DuplexBuffer<Page>(Pages.MAX_RELATIVE_INDEX)
    private val updateMutex = Object()  // для синхронизации между GL потоком и Main потоком

    init {
        sized.onChanged.subscribe {
            synchronized(updateMutex) {
                sized.forEachPageIndexed { i, page -> sizedPagesSnapshot[i] = page }
                onChanged()
            }
        }
    }

    fun destroy() {
        sized.destroy()
    }

    fun reformat() {
        sized.reformat()
        onChanged()
    }

    fun goLocation(location: Location) = synchronized(updateMutex) {
        sized.goLocation(location)
        animation.goPage()
        onChanged()
    }

    fun goNextPage() = synchronized(updateMutex) {
        if (canGoNextPage()) {
            sized.goNextPage()
            animation.goNextPage()
            onChanged()
        }
    }

    fun goPreviousPage() = synchronized(updateMutex) {
        if (canGoPreviousPage()) {
            sized.goPreviousPage()
            animation.goPreviousPage()
            onChanged()
        }
    }

    private fun canGoNextPage(): Boolean {
        val slideNotTooFar = animation.hasSlides && animation.firstSlideIndex >= -Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && sized.canGoNextPage()
    }

    private fun canGoPreviousPage(): Boolean {
        val slideNotTooFar = animation.hasSlides && animation.lastSlideIndex <= Pages.MAX_RELATIVE_INDEX
        return slideNotTooFar && sized.canGoPreviousPage()
    }

    /**
     * Вызывается из GL потока
     */
    fun update() = synchronized(updateMutex) {
        val wasAnimating = animation.isAnimating

        animation.update()
        updatePagesAndSlides()

        if (animation.isAnimating)
            onChanged()

        if (wasAnimating && !animation.isAnimating) {
            onStopAnimate()
        }
    }

    private fun onStopAnimate() {
        runOn(mainScheduler) {
            checkNextPageIsValid()
        }
    }

    private fun checkNextPageIsValid() = synchronized(updateMutex) {
        if (!animation.isAnimating)
            sized.checkNextPageIsValid()
    }

    private fun updatePagesAndSlides() {
        loadedPages.clear()
        visibleSlides.clear()

        animation.slides.forEach { slide ->
            val page = addPage(sizedPagesSnapshot, slide.relativeIndex)
            visibleSlides.add(Slide(page, slide.offsetX))
        }

        if (animation.hasSlides) {
            if (!animation.isAnimating || animation.isGoingNext) {
                addPage(sizedPagesSnapshot, animation.lastSlideIndex + 1)
                addPage(sizedPagesSnapshot, animation.firstSlideIndex - 1)
            } else {
                addPage(sizedPagesSnapshot, animation.firstSlideIndex - 1)
                addPage(sizedPagesSnapshot, animation.lastSlideIndex + 1)
            }
        }
    }

    private fun addPage(pages: DuplexBuffer<Page>, relativeIndex: Int): Page? {
        if (loadedPages.size < MAX_LOADED_PAGES && Math.abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) {
            val page = pages[relativeIndex]
            if (page != null) {
                loadedPages.add(page)
                return page
            }
        }
        return null
    }

    private fun onChanged() = onChanged.onNext(Unit)

    class Slide(val page: Page?, val offsetX: Float)
}