package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange

class RenderBox(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        range: LocationRange
) : RenderObject(width, height, children, range) {
    override fun canBeSeparated() = true
}