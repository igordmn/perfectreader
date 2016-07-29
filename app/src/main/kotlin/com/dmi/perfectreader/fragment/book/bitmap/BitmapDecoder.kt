package com.dmi.perfectreader.fragment.book.bitmap

import android.graphics.Bitmap
import com.dmi.util.graphic.Size

interface BitmapDecoder {
    fun loadDimensions(src: String): Size
    fun decode(src: String, width: Int, height: Int, scaleFiltered: Boolean): Bitmap
}