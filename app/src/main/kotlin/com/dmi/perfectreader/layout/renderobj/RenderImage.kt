package com.dmi.perfectreader.layout.renderobj

import android.graphics.*
import com.dmi.perfectreader.location.BookRange

class RenderImage(
        width: Float,
        height: Float,
        val bitmap: Bitmap,
        range: BookRange
) : RenderObject(width, height, emptyList(), range) {
    companion object {
        private val paint = Paint()
    }

    override fun paintItself(canvas: Canvas) {
        if (bitmap.width.toFloat() == width && bitmap.height.toFloat() == height) {
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
        } else {
            canvas.drawBitmap(
                    bitmap,
                    Rect(0, 0, bitmap.width, bitmap.height),
                    RectF(0F, 0F, width, height),
                    paint
            )
        }
    }
}