package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import rx.lang.kotlin.PublishSubject

class StaticBook(
        private val createPages: () -> Pages,
        private val createPagesLoader: (Pages) -> PagesLoader
) {
    private lateinit var pages: Pages
    private lateinit var pagesLoader: PagesLoader

    val location: Location get() = pages.location

    val onPagesChanged = PublishSubject<Unit>()

    init {
        initPages()
    }

    private fun initPages() {
        pages = createPages()
        pagesLoader = createPagesLoader(pages)
        pagesLoader.onLoad.subscribe(onPagesChanged)
        pagesLoader.check()
    }

    fun destroy() {
        pagesLoader.destroy()
    }

    fun reformat() {
        pagesLoader.destroy()
        initPages()
        onPagesChanged.onNext(Unit)
    }

    fun goLocation(location: Location) {
        pages.goLocation(location)
        pagesLoader.check()
        onPagesChanged.onNext(Unit)
    }

    fun goNextPage() {
        require(canGoNextPage())
        pages.goNextPage()
        pagesLoader.check()
        onPagesChanged.onNext(Unit)
    }

    fun goPreviousPage() {
        require(canGoPreviousPage())
        pages.goPreviousPage()
        pagesLoader.check()
        onPagesChanged.onNext(Unit)
    }

    fun canGoNextPage() = pages.canGoNextPage()
    fun canGoPreviousPage() = pages.canGoPreviousPage()

    fun checkNextPageIsValid() {
        if (!pages.isNextPagesValid()) {
            pages.fixPages()
            pagesLoader.check()
            onPagesChanged.onNext(Unit)
        }
    }

    fun pageAt(relativeIndex: Int): Page? = pages[relativeIndex]
}