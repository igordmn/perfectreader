package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.common.LayoutLength

class LayoutFrame(
        val paddings: Paddings,
        val borders: Borders,
        val child: LayoutObject
) : LayoutObject() {
    class Border(val width: Float, val color: Int)
    class Paddings(val left: LayoutLength, val right: LayoutLength, val top: LayoutLength, val bottom: LayoutLength)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}
