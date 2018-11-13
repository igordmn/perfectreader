package com.dmi.perfectreader.ui.book.render.obj

import android.graphics.*
import com.dmi.util.android.graphics.correctAlphaGamma
import java.lang.ref.WeakReference

private var bufferRef = WeakReference<Bitmap>(null)
private var bufferWidth = 0
private var bufferHeight = 0

private fun buffer(width: Int, height: Int): Bitmap {
    var buffer: Bitmap? = bufferRef.get()
    if (buffer == null || bufferWidth != width || bufferHeight != height) {
        buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bufferRef = WeakReference(buffer)
        bufferWidth = width
        bufferHeight = height
    }
    return buffer!!
}

private val bufferPaint = Paint()
private val bufferCanvas = Canvas()

class RenderPicture(
        private val picture: Picture,
        private val alphaGamma: Float = 1F
) : RenderObject() {
    override fun dirtyRect(oldContext: Context, newContext: Context) = null

    override fun paint(canvas: Canvas, context: Context) {
        if (alphaGamma == 1F) {
            canvas.drawPicture(picture)
        } else {
            val clipRect = canvas.clipBounds
            val buffer = buffer(picture.width, picture.height)
            bufferCanvas.setBitmap(buffer)
            bufferCanvas.save()
            bufferCanvas.clipRect(clipRect)
            bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            bufferCanvas.drawPicture(picture)
            bufferCanvas.restore()
            correctAlphaGamma(buffer, clipRect.left, clipRect.top, clipRect.width(), clipRect.height(), alphaGamma)
            canvas.drawBitmap(buffer, 0F, 0F, bufferPaint)
        }
    }
}