package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.graphic.Color

class RenderFrame(
        width: Float,
        height: Float,
        val internalMargins: Margins,
        val borders: Borders,
        val background: Background,
        val child: RenderChild,
        range: LocationRange
) : RenderObject(width, height, listOf(child), range) {
    override fun canBeSeparated() = true
    override fun internalMargins() = internalMargins

    class Background(val color: Color)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}