package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel
import com.dmi.perfectreader.fragment.book.page.SlidePagesAnimation
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.graphic.SizeF
import rx.lang.kotlin.PublishSubject

class BookRenderModel(size: SizeF) {
    companion object {
        private val SINGLE_SLIDE_SECONDS = 0.4F
    }

    private val pagesModel = DuplexBuffer<Page>(Pages.MAX_RELATIVE_INDEX)

    private val animation = SlidePagesAnimation(size.width, SINGLE_SLIDE_SECONDS)
    val pages = PagesRenderModel()

    val onChanged = PublishSubject<Unit>()
    val onStopAnimate = PublishSubject<Unit>()

    private val renderMutex = Any()

    val isAnimating: Boolean get() = animation.isAnimating

    fun goPage() = synchronized (renderMutex) {
        pagesModel.clear()
        animation.goPage()
        onChanged()
        if (!animation.isAnimating)
            onStopAnimate()
    }

    fun canGoNextPage() = synchronized (renderMutex) {
        pages.canGoNextPage(animation)
    }

    fun canGoPreviousPage() = synchronized (renderMutex) {
        pages.canGoPreviousPage(animation)
    }

    fun goNextPage() = synchronized (renderMutex) {
        pagesModel.shiftLeft()
        animation.goNextPage()
        onChanged()
        if (!animation.isAnimating)
            onStopAnimate()
    }

    fun goPreviousPage() = synchronized (renderMutex) {
        pagesModel.shiftRight()
        animation.goPreviousPage()
        onChanged()
        if (!animation.isAnimating)
            onStopAnimate()
    }

    fun setPages(pages: Pages) = synchronized (renderMutex) {
        pages.forEachIndexed { i, page -> pagesModel[i] = page }
        onChanged()
    }

    /**
     * Вызывается из OpenGL потока
     */
    fun update() = synchronized (renderMutex) {
        val wasAnimating = animation.isAnimating

        animation.update()
        pages.set(animation, pagesModel)

        if (animation.isAnimating)
            onChanged()

        if (wasAnimating && !animation.isAnimating) {
            onStopAnimate()
        }
    }

    private fun onChanged() = onChanged.onNext(Unit)
    private fun onStopAnimate() = onStopAnimate.onNext(Unit)
}