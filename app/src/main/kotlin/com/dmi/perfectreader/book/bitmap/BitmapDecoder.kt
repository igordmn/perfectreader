package com.dmi.perfectreader.book.bitmap

import android.graphics.Bitmap
import com.dmi.util.graphic.Size

interface BitmapDecoder {
    fun loadSize(src: String): Size
    fun decode(src: String, width: Int, height: Int, scaleFiltered: Boolean): Bitmap
}