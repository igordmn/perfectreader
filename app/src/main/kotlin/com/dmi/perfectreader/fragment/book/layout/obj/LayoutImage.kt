package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutImage(
        width: Float,
        height: Float,
        val bitmapWidth: Int,
        val bitmapHeight: Int,
        val scaleFiltered: Boolean,
        val src: String?,
        range: LocationRange
) : LayoutObject(width, height, emptyList(), range)