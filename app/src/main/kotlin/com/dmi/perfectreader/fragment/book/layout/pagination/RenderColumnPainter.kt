package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas

class RenderColumnPainter(private val renderPartPainter: RenderPartPainter) {
    fun paint(column: RenderColumn, canvas: Canvas) {
        with (column) {
            canvas.save()
            for (i in 0..parts.size - 1) {
                val row = parts[i]
                renderPartPainter.paint(row, canvas)
                canvas.translate(0F, row.height)
            }
            canvas.restore()
        }
    }
}