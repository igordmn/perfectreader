package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import rx.lang.kotlin.PublishSubject

class SizedBook(
        private val createPages: () -> Pages,
        private val createPagesLoader: (Pages) -> PagesLoader
) {
    private lateinit var pages: Pages
    private lateinit var pagesLoader: PagesLoader

    val location: Location get() = pages.location
    var selectionRange: LocationRange? = null
        set(value) {
            field = value
            pageContext = PageContext(value)
            this.onPagesChanged.onNext(Unit)
        }

    var pageContext: PageContext = PageContext(null)
        private set
    val onPagesChanged = PublishSubject<Unit>()

    init {
        initPages()
    }

    private fun initPages() {
        pages = createPages()
        pagesLoader = createPagesLoader(pages)
        pagesLoader.onLoad.subscribe(this.onPagesChanged)
        pagesLoader.check()
    }

    fun destroy() {
        pagesLoader.destroy()
    }

    fun reformat() {
        pagesLoader.destroy()
        initPages()
        this.onPagesChanged.onNext(Unit)
        this.onPagesChanged.onNext(Unit)
    }

    fun goLocation(location: Location) {
        pages.goLocation(location)
        pagesLoader.check()
        this.onPagesChanged.onNext(Unit)
    }

    fun goNextPage() {
        require(canGoNextPage())
        pages.goNextPage()
        pagesLoader.check()
        this.onPagesChanged.onNext(Unit)
    }

    fun goPreviousPage() {
        require(canGoPreviousPage())
        pages.goPreviousPage()
        pagesLoader.check()
        this.onPagesChanged.onNext(Unit)
    }

    fun canGoNextPage() = pages.canGoNextPage()
    fun canGoPreviousPage() = pages.canGoPreviousPage()

    fun checkNextPageIsValid() {
        pages.checkNextPageIsValid()
        pagesLoader.check()
        this.onPagesChanged.onNext(Unit)
    }

    fun pageAt(relativeIndex: Int): Page? = pages[relativeIndex]

    fun forEachPageIndexed(action: (index: Int, page: Page?) -> Unit) = pages.forEachIndexed(action)
}