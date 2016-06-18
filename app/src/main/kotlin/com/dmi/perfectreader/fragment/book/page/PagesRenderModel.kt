package com.dmi.perfectreader.fragment.book.page

import com.dmi.perfectreader.fragment.book.layout.pagination.Page
import com.dmi.util.collection.DuplexBuffer
import java.lang.Math.abs
import java.util.*

class PagesRenderModel {
    companion object {
        val MAX_LOADED_PAGES = 3
    }

    val loadingPage = LoadingPage()
    val loadedPages = LinkedHashSet<Page>()
    val visibleSlides = ArrayList<Slide>()

    fun apply(animation: SlidePagesAnimation, pages: DuplexBuffer<Page>) {
        loadedPages.clear()
        visibleSlides.clear()

        animation.visibleSlides.forEach { slide ->
            val page = addLoadedPage(pages, slide.relativeIndex)
            visibleSlides.add(Slide(page, slide.offsetX))
        }

        if (animation.hasSlides) {
            addLoadedPage(pages, animation.firstSlideIndex - 1)
            addLoadedPage(pages, animation.lastSlideIndex + 1)
        }
    }

    fun canGoNextPage(animation: SlidePagesAnimation) = animation.firstSlideIndex >= -Pages.MAX_RELATIVE_INDEX
    fun canGoPreviousPage(animation: SlidePagesAnimation) = animation.lastSlideIndex <= Pages.MAX_RELATIVE_INDEX

    private val SlidePagesAnimation.hasSlides: Boolean get() = visibleSlides.size > 0
    private val SlidePagesAnimation.firstSlideIndex: Int get() = visibleSlides.first().relativeIndex
    private val SlidePagesAnimation.lastSlideIndex: Int get() = visibleSlides.last().relativeIndex

    private fun addLoadedPage(pages: DuplexBuffer<Page>, relativeIndex: Int): Page? {
        if (loadedPages.size < MAX_LOADED_PAGES && abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) {
            val page = pages[relativeIndex]
            if (page != null) {
                loadedPages.add(page)
                return page
            }
        }
        return null
    }

    class Slide(val page: Page?, val offsetX: Float)

    class LoadingPage
}