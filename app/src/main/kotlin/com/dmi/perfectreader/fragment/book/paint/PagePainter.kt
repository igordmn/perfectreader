package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.Page

class PagePainter(private val columnPainter: ColumnPainter) {
    fun paint(page: Page, canvas: Canvas) {
        val column = page.column
        val contentSize = page.contentSize
        val margins = page.margins

        val pageWidth = margins.left + contentSize.width + margins.right
        val contentHeight = column.height

        canvas.save()

        canvas.clipRect(0F, margins.top, pageWidth, margins.top + contentHeight)
        canvas.translate(margins.left, margins.top)
        columnPainter.paint(column, canvas, PaintContext(null))

        canvas.restore()
    }
}