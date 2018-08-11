package com.dmi.perfectreader.book.layout.obj

import com.dmi.perfectreader.book.location.LocationRange

class LayoutImage(
        width: Float,
        height: Float,
        val bitmapWidth: Int,
        val bitmapHeight: Int,
        val scaleFiltered: Boolean,
        val src: String?,
        range: LocationRange
) : LayoutObject(width, height, emptyList(), range) {
    override fun isClickable() = true
}