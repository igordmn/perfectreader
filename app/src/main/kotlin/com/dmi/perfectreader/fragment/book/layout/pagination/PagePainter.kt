package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas

class PagePainter(private val renderColumnPainter: RenderColumnPainter) {
    fun paint(page: Page, canvas: Canvas) {
        val column = page.column
        val contentSize = page.contentSize
        val margins = page.margins

        val pageWidth = margins.left + contentSize.width + margins.right
        val contentHeight = column.height

        canvas.save()

        canvas.clipRect(0F, margins.top, pageWidth, margins.top + contentHeight)
        canvas.translate(margins.left, margins.top)
        renderColumnPainter.paint(column, canvas)

        canvas.restore()
    }
}