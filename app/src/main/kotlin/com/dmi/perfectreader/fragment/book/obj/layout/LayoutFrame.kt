package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.Length
import com.dmi.util.graphic.Color

class LayoutFrame(
        val margins: Margins,
        val paddings: Paddings,
        val borders: Borders,
        val background: Background,
        val child: LayoutObject,
        range: LocationRange
) : LayoutObject(range) {
    class Background(val color: Color)
    class Margins(val left: Length, val right: Length, val top: Length, val bottom: Length)
    class Paddings(val left: Length, val right: Length, val top: Length, val bottom: Length)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}