package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutParagraph(
        width: Float,
        height: Float,
        children: List<LayoutChild>,
        range: LocationRange
) : LayoutObject(width, height, children, range) {
    override fun canBeSeparated() = true
}