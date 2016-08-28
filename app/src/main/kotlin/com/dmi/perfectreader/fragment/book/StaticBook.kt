package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageConfig
import rx.lang.kotlin.PublishSubject

class StaticBook(
        createPages: () -> Pages,
        private val createPageConfig: () -> PageConfig,
        private val createPagesLoader: (Pages, PageConfig) -> PagesLoader,
        private val createLocationConverter: (PageConfig) -> LocationConverter
) {
    private var pages: Pages = createPages()
    private var pageConfig: PageConfig = createPageConfig()
    private var pagesLoader: PagesLoader = initPagesLoader(pageConfig)

    val location: Location get() = pages.location
    var locationConverter: LocationConverter = createLocationConverter(pageConfig)
        private set

    val onPagesChanged = PublishSubject<Unit>()

    private fun initPagesLoader(pageConfig: PageConfig) = createPagesLoader(pages, pageConfig).apply {
        onLoad.subscribe {
            onPagesChanged.onNext(Unit)
        }
    }

    init {
        pagesLoader.check()
    }

    fun destroy() {
        pagesLoader.destroy()
    }

    fun reformat() {
        pages.needReloadAll()
        pagesLoader.destroy()
        pageConfig = createPageConfig()
        pagesLoader = initPagesLoader(pageConfig)
        pagesLoader.check()
        locationConverter = createLocationConverter(pageConfig)
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
            pages.needReloadRight()
            pagesLoader.check()
        }
    }

    fun pageAt(relativeIndex: Int): Page? = pages[relativeIndex]
}