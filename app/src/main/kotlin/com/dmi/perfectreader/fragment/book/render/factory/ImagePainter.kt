package com.dmi.perfectreader.fragment.book.render.factory

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage

class ImagePainter(private val bitmapDecoder: BitmapDecoder) {
    companion object {
        private val EMPTY = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    private val paint = Paint()

    fun paint(x: Float, y: Float, obj: LayoutImage, canvas: Canvas) {
        val bitmap = if (obj.src != null) {
            bitmapDecoder.decode(obj.src, obj.bitmapWidth, obj.bitmapHeight, obj.scaleFiltered)
        } else {
            EMPTY
        }
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}