package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.painter.PaintContext

class RenderColumnPainter(private val renderPartPainter: RenderPartPainter) {
    fun paint(column: RenderColumn, canvas: Canvas, context: PaintContext) {
        with (column) {
            canvas.save()
            parts.forEach {
                renderPartPainter.paint(it, canvas, context)
                canvas.translate(0F, it.height)
            }
            canvas.restore()
        }
    }
}