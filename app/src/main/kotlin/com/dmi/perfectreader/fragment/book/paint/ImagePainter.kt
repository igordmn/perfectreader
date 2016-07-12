package com.dmi.perfectreader.fragment.book.paint

import android.graphics.*
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
        val bitmap = if (obj.src != null) bitmapDecoder.decode(obj.src, obj.width, obj.height) else EMPTY
        if (bitmap.width.toFloat() == obj.width && bitmap.height.toFloat() == obj.height) {
            canvas.drawBitmap(bitmap, 0F, 0F, paint)
        } else {
            canvas.drawBitmap(
                    bitmap,
                    Rect(0, 0, bitmap.width, bitmap.height),
                    RectF(0F, 0F, obj.width, obj.height),
                    paint
            )
        }
    }
}