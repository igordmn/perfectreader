package com.dmi.perfectreader.layout.layoutobj

import com.dmi.perfectreader.layout.layoutobj.common.LayoutLength
import com.dmi.perfectreader.location.BookRange

class LayoutFrame(
        val margins: Margins,
        val paddings: Paddings,
        val borders: Borders,
        val background: Background,
        val child: LayoutObject,
        range: BookRange
) : LayoutObject(range) {
    class Background(val color: Int)
    class Margins(val left: LayoutLength, val right: LayoutLength, val top: LayoutLength, val bottom: LayoutLength)
    class Paddings(val left: LayoutLength, val right: LayoutLength, val top: LayoutLength, val bottom: LayoutLength)
    class Border(val width: Float, val color: Int)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}
