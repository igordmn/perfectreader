package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.painter.PaintContext

class LayoutColumnPainter(private val layoutPartPainter: LayoutPartPainter) {
    fun paint(column: LayoutColumn, canvas: Canvas, context: PaintContext) {
        with (column) {
            canvas.save()
            parts.forEach {
                layoutPartPainter.paint(it, canvas, context)
                canvas.translate(0F, it.height)
            }
            canvas.restore()
        }
    }
}