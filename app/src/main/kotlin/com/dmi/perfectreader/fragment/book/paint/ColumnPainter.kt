package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn

class ColumnPainter(private val partPainter: PartPainter) {
    fun paint(column: LayoutColumn, canvas: Canvas, context: PaintContext) {
        with (column) {
            canvas.save()
            parts.forEach {
                partPainter.paint(it, canvas, context)
                canvas.translate(0F, it.height)
            }
            canvas.restore()
        }
    }
}