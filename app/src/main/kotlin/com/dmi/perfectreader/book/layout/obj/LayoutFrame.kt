package com.dmi.perfectreader.book.layout.obj

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.graphic.Color

class LayoutFrame(
        width: Float,
        height: Float,
        val internalMargins: Margins,
        val borders: Borders,
        val background: Background,
        val child: LayoutChild,
        pageBreakBefore: Boolean,
        range: LocationRange
) : LayoutObject(width, height, listOf(child), range, pageBreakBefore) {
    override fun canBeSeparated() = true
    override fun internalMargins() = internalMargins

    class Background(val color: Color)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}