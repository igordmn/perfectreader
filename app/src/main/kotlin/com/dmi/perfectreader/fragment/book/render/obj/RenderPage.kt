package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.paint.PagePainter
import com.dmi.util.graphic.Rect

// todo задействовать dirtyRect
class RenderPage(
        private val page: Page,
        private val pagePainter: PagePainter
) {
    fun dirtyRect(oldContext: PageContext?, newContext: PageContext): Rect {
        if (newContext == oldContext) {
            return Rect(0, 0, 0, 0)
        } else {
            return Rect(0, 1, 0, 1)
        }
    }

    fun paint(context: PageContext, canvas: Canvas, rect: Rect) {
        pagePainter.paint(page, context, canvas)
    }
}