package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.fragment.book.render.obj.RenderImage

class ImagePainter {
    private val paint = Paint()

    fun paint(obj: RenderImage, canvas: Canvas) {
        canvas.drawBitmap(obj.bitmap, obj.x, obj.y, paint)
    }
}