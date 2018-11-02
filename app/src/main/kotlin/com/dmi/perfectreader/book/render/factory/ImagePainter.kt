package com.dmi.perfectreader.book.render.factory

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.layout.obj.LayoutImage
import kotlinx.io.IOException

class ImagePainter(private val bitmapDecoder: BitmapDecoder) {
    companion object {
        private val EMPTY = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    private val paint = Paint()

    fun paint(x: Float, y: Float, obj: LayoutImage, canvas: Canvas) {
        val bitmap = if (obj.src != null) {
            try {
                bitmapDecoder.decode(obj.src, obj.bitmapWidth, obj.bitmapHeight, obj.scaleFiltered)
            } catch (e: IOException) {
                EMPTY
            }
        } else {
            EMPTY
        }
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}