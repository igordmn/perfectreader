package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutLine(
        width: Float,
        height: Float,
        val blankVerticalMargins: Float,
        children: List<LayoutChild>,
        range: LocationRange
) : LayoutObject(width, height, children, range) {
    private val internalMargins = Margins(0F, 0F, blankVerticalMargins, blankVerticalMargins)

    override fun toString(): String = children
            .map { it.obj }
            .filter { it is LayoutText }
            .map { it.toString() }
            .joinToString("")

    override fun internalMargins() = internalMargins
}