package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange

class RenderImage(
        width: Float,
        height: Float,
        range: LocationRange,
        val src: String?
) : RenderObject(width, height, emptyList(), range)