package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.page.Pages
import com.dmi.perfectreader.fragment.book.page.PagesLoader
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.mainScheduler

class SizedBook(
        private val createPages: () -> Pages,
        private val createPagesLoader: (Pages) -> PagesLoader,
        val renderModel: BookRenderModel
) : BaseViewModel() {
    private lateinit var pages: Pages
    private lateinit var pagesLoader: PagesLoader
    val location: Location get() = pages.location

    init {
        initPages()
        renderModel.onStopAnimate.subscribeOn(mainScheduler).subscribe {
            checkNextPageIsValid()
        }
    }

    private fun initPages() {
        pages = createPages()
        pagesLoader = createPagesLoader(pages)
        renderModel.setPages(pages)
        pagesLoader.onLoad.subscribe {
            renderModel.setPages(pages)
        }
        pagesLoader.check()
    }

    override fun destroy() {
        super.destroy()
        pagesLoader.destroy()
    }

    fun reformat() {
        pagesLoader.destroy()
        initPages()
    }

    fun goLocation(location: Location) {
        pages.goLocation(location)
        renderModel.goPage()
        pagesLoader.check()
    }

    private fun canGoNextPage() = renderModel.canGoNextPage() && pages.canGoNextPage()
    private fun canGoPreviousPage() = renderModel.canGoPreviousPage() && pages.canGoPreviousPage()

    fun goNextPage() {
        if (canGoNextPage()) {
            pages.goNextPage()
            renderModel.goNextPage()
            pagesLoader.check()
        }
    }

    fun goPreviousPage() {
        if (canGoPreviousPage()) {
            pages.goPreviousPage()
            renderModel.goPreviousPage()
            pagesLoader.check()
        }
    }

    private fun checkNextPageIsValid() {
        if (!renderModel.isAnimating) {
            pages.checkNextPageIsValid()
            pagesLoader.check()
        }
    }
}