package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutLine(
        width: Float,
        height: Float,
        children: List<LayoutChild>,
        range: LocationRange
) : LayoutObject(width, height, children, range)