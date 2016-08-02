package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas

class BitmapBuffer(val width: Int, val height: Int) {
    val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas: Canvas = Canvas(bitmap)
}