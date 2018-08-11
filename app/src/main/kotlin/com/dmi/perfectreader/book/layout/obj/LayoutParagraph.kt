package com.dmi.perfectreader.book.layout.obj

import com.dmi.perfectreader.book.content.location.LocationRange

class LayoutParagraph(
        width: Float,
        height: Float,
        children: List<LayoutChild>,
        range: LocationRange
) : LayoutObject(width, height, children, range) {
    override fun canBeSeparated() = true
}