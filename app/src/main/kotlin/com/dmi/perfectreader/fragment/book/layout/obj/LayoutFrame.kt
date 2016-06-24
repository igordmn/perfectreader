package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.graphic.Color

class LayoutFrame(
        width: Float,
        height: Float,
        val internalMargins: Margins,
        val borders: Borders,
        val background: Background,
        val child: LayoutChild,
        range: LocationRange
) : LayoutObject(width, height, listOf(child), range) {
    override fun canBeSeparated() = true
    override fun internalMargins() = internalMargins

    class Background(val color: Color)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}