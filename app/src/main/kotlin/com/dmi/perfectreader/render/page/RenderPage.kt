package com.dmi.perfectreader.render.page

import android.graphics.Canvas
import com.dmi.perfectreader.location.BookRange

class RenderPage(
        val rows: List<RenderRow> = emptyList(),
        val height: Float,
        val range: BookRange
) {
    fun paint(canvas: Canvas) {
        canvas.save()
        for (i in 0..rows.size - 1) {
            val row = rows[i]
            row.paint(canvas)
            canvas.translate(0F, row.height)
        }
        canvas.restore()
    }
}
