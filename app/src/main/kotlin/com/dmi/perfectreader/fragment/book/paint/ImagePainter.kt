package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext

class ImagePainter(
        private val bitmapDecoder: BitmapDecoder
) : ObjectPainter<LayoutImage> {
    companion object {
        private val EMPTY = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    private val paint = Paint()

    override fun paintItself(obj: LayoutImage, context: PageContext, canvas: Canvas) {
        paint.isFilterBitmap = obj.scaleFiltered
        val bitmap = if (obj.src != null) {
            bitmapDecoder.decode(obj.src, obj.width.toInt(), obj.height.toInt(), obj.scaleFiltered)
        } else {
            EMPTY
        }
        canvas.drawBitmap(bitmap, 0F, 0F, paint)
    }
}