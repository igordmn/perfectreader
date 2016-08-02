package com.dmi.perfectreader.fragment.book.render

import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.paint.PagePainter
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage

class PageRenderer(private val pagePainter: PagePainter) {
    fun render(page: Page): RenderPage {
        return RenderPage(page, pagePainter)
    }
}