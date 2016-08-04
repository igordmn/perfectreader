package com.dmi.perfectreader.fragment.book.render.render

import android.graphics.Bitmap
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage
import com.dmi.perfectreader.fragment.book.render.obj.RenderImage
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import java.util.*

class ImageRenderer(private val bitmapDecoder: BitmapDecoder) {
    companion object {
        private val EMPTY = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    fun render(x: Float, y: Float, image: LayoutImage, objects: ArrayList<RenderObject>) {
        val bitmap = if (image.src != null) {
            bitmapDecoder.decode(image.src, image.bitmapWidth, image.bitmapHeight, image.scaleFiltered)
        } else {
            EMPTY
        }
        objects.add(RenderImage(x, y, image, bitmap))
    }
}