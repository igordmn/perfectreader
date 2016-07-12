package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutLine(
        width: Float,
        height: Float,
        children: List<LayoutChild>,
        range: LocationRange
) : LayoutObject(width, height, children, range) {
    override fun toString(): String = children
            .map { it.obj }
            .filter { it is LayoutText }
            .map { it.toString() }
            .joinToString("")
}