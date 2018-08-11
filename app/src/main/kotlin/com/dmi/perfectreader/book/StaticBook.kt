package com.dmi.perfectreader.book

import com.dmi.perfectreader.book.location.Location
import com.dmi.perfectreader.book.page.Pages
import com.dmi.perfectreader.book.page.PagesLoader
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.book.pagination.page.PageConfig
import com.dmi.util.lang.clamp
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import java.lang.Math.*

class StaticBook(
        private val createPages: () -> Pages,
        private val createPageConfig: () -> PageConfig,
        private val createPagesLoader: (Pages, PageConfig) -> PagesLoader,
        private val createLocationConverter: (PageConfig) -> LocationConverter
) {
    private var pages: Pages = createPages()
    private var pageConfig: PageConfig = createPageConfig()
    private var pagesLoader: PagesLoader = initPagesLoader(pages, pageConfig)

    val locationObservable = BehaviorSubject(pages.location)
    var location: Location = pages.location
        set(value) {
            field = value
            locationObservable.onNext(value)
        }

    var locationConverter: LocationConverter = createLocationConverter(pageConfig)
        private set

    val onPagesChanged = PublishSubject<Unit>()

    val pageNumber: Int get() = locationConverter.locationToPageNumber(location)
    val numberOfPages: Int get() = locationConverter.numberOfPages
    val minGoRelativeIndex: Int get() = min(pages.minGoRelativeIndex, 1 - pageNumber)
    val maxGoRelativeIndex: Int get() = max(pages.maxGoRelativeIndex, numberOfPages - pageNumber)

    private fun initPagesLoader(pages: Pages, pageConfig: PageConfig) = createPagesLoader(pages, pageConfig).apply {
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
        pages = createPages()
        pagesLoader.destroy()
        pageConfig = createPageConfig()
        pagesLoader = initPagesLoader(pages, pageConfig)
        pagesLoader.check()
        locationConverter = createLocationConverter(pageConfig)
        onPagesChanged.onNext(Unit)
    }

    fun goLocation(location: Location) {
        pages.goLocation(location)
        pagesLoader.check()
        this.location = location
        onPagesChanged.onNext(Unit)
    }

    fun goPage(relativeIndex: Int) = goLimitedPage(clamp(relativeIndex, minGoRelativeIndex, maxGoRelativeIndex))

    private fun goLimitedPage(relativeIndex: Int) {
        if (relativeIndex == 0) return

        val currentPage = pageAt(0)
        if (relativeIndex >= pages.minGoRelativeIndex && relativeIndex <= pages.maxGoRelativeIndex) {
            pages.goPage(relativeIndex)
            if (!pages.isNextPagesValid())
                pages.needReloadRight()
            pagesLoader.check()
            location = pages.location
            onPagesChanged.onNext(Unit)
        } else if (relativeIndex == 1 && currentPage != null) {
            goLocation(currentPage.range.end)
        } else {
            goLocation(locationConverter.pageNumberToLocation(pageNumber + relativeIndex))
        }
    }

    fun pageAt(relativeIndex: Int): Page? = if (abs(relativeIndex) <= Pages.MAX_RELATIVE_INDEX) pages[relativeIndex] else null
}