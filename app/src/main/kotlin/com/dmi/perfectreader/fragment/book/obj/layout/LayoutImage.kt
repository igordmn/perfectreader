package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutImage(
        width: Float,
        height: Float,
        range: LocationRange,
        val src: String?
) : LayoutObject(width, height, emptyList(), range)