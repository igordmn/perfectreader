package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutSize

class LayoutImage(
        val size: LayoutSize,
        val src: String?,
        range: LocationRange
) : LayoutObject(range)