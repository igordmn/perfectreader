package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas

class RenderColumnPainter(private val renderPartPainter: RenderPartPainter) {
    fun paint(column: RenderColumn, canvas: Canvas) {
        with (column) {
            canvas.save()
            parts.forEach {
                renderPartPainter.paint(it, canvas)
                canvas.translate(0F, it.height)
            }
            canvas.restore()
        }
    }
}