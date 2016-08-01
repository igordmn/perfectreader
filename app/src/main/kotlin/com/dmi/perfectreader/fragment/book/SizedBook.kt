package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import rx.lang.kotlin.PublishSubject

class SizedBook(
        private val createPages: () -> Pages,
        private val createPagesLoader: (Pages) -> PagesLoader
) {
    private lateinit var pages: Pages
    private lateinit var pagesLoader: PagesLoader

    val location: Location get() = pages.location
    val onChanged = PublishSubject<Unit>()

    init {
        initPages()
    }

    private fun initPages() {
        pages = createPages()
        pagesLoader = createPagesLoader(pages)
        pagesLoader.onLoad.subscribe(onChanged)
        pagesLoader.check()
    }

    fun destroy() {
        pagesLoader.destroy()
    }

    fun reformat() {
        pagesLoader.destroy()
        initPages()
        onChanged.onNext(Unit)
    }

    fun goLocation(location: Location) {
        pages.goLocation(location)
        pagesLoader.check()
        onChanged.onNext(Unit)
    }

    fun goNextPage() {
        require(canGoNextPage())
        pages.goNextPage()
        pagesLoader.check()
        onChanged.onNext(Unit)
    }

    fun goPreviousPage() {
        require(canGoPreviousPage())
        pages.goPreviousPage()
        pagesLoader.check()
        onChanged.onNext(Unit)
    }

    fun canGoNextPage() = pages.canGoNextPage()
    fun canGoPreviousPage() = pages.canGoPreviousPage()

    fun checkNextPageIsValid() {
        pages.checkNextPageIsValid()
        pagesLoader.check()
        onChanged.onNext(Unit)
    }

    fun forEachPageIndexed(action: (index: Int, page: Page?) -> Unit) = pages.forEachIndexed(action)
}