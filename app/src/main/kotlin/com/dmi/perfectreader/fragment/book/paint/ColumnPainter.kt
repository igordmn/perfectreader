package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext

class ColumnPainter(private val partPainter: PartPainter) {
    fun paint(column: LayoutColumn, context: PageContext, canvas: Canvas) {
        with (column) {
            canvas.save()
            parts.forEach {
                partPainter.paint(it, context, canvas)
                canvas.translate(0F, it.height)
            }
            canvas.restore()
        }
    }
}