package com.dmi.perfectreader.fragment.book.page

import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.collection.DuplexBuffer
import java.lang.Math.abs
import java.util.*

class PagesRenderModel {
    companion object {
        val MAX_LOADED_PAGES = 3
    }

    val pageSet = LinkedHashSet<Page>()
    val visibleSlides = ArrayList<Slide>()

    fun set(animation: SlidePagesAnimation, pages: DuplexBuffer<Page>) {
        pageSet.clear()
        visibleSlides.clear()

        animation.visibleSlides.forEach { slide ->
            val page = addPage(pages, slide.relativeIndex)
            visibleSlides.add(Slide(page, slide.offsetX))
        }

        if (animation.hasSlides) {
            addPage(pages, animation.firstSlideIndex - 1)
            addPage(pages, animation.lastSlideIndex + 1)
        }
    }

    fun canGoNextPage(animation: SlidePagesAnimation) = animation.firstSlideIndex >= -Pages.MAX_RELATIVE_INDEX
    fun canGoPreviousPage(animation: SlidePagesAnimation) = animation.lastSlideIndex <= Pages.MAX_RELATIVE_INDEX

    private val SlidePagesAnimation.hasSlides: Boolean get() = visibleSlides.size > 0
    private val SlidePagesAnimation.firstSlideIndex: Int get() = visibleSlides.first().relativeIndex
    private val SlidePagesAnimation.lastSlideIndex: Int get() = visibleSlides.last().relativeIndex

    private fun addPage(pages: DuplexBuffer<Page>, relativeIndex: Int): Page? {
        if (pageSet.size < MAX_LOADED_PAGES && abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) {
            val page = pages[relativeIndex]
            if (page != null) {
                pageSet.add(page)
                return page
            }
        }
        return null
    }

    class Slide(val page: Page?, val offsetX: Float)
}