package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.painter.PaintContext

class PagePainter(private val layoutColumnPainter: LayoutColumnPainter) {
    fun paint(page: Page, canvas: Canvas) {
        val column = page.column
        val contentSize = page.contentSize
        val margins = page.margins

        val pageWidth = margins.left + contentSize.width + margins.right
        val contentHeight = column.height

        canvas.save()

        canvas.clipRect(0F, margins.top, pageWidth, margins.top + contentHeight)
        canvas.translate(margins.left, margins.top)
        layoutColumnPainter.paint(column, canvas, PaintContext(null))

        canvas.restore()
    }
}