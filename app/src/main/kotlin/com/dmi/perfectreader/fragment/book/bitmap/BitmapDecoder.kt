package com.dmi.perfectreader.fragment.book.bitmap

import android.graphics.Bitmap
import com.dmi.util.graphic.SizeF

interface BitmapDecoder {
    fun loadDimensions(src: String): SizeF
    fun decode(src: String, maxWidth: Float, maxHeight: Float): Bitmap
}