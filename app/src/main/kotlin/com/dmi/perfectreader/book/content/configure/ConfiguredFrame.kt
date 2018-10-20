package com.dmi.perfectreader.book.content.configure

import com.dmi.perfectreader.book.content.configure.common.ConfiguredLength
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.graphic.Color

class ConfiguredFrame(
        val margins: Margins,
        val paddings: Paddings,
        val borders: Borders,
        val background: Background,
        val child: ConfiguredObject,
        override val range: LocationRange
) : ConfiguredObject {
    class Background(val color: Color)
    class Margins(val left: ConfiguredLength, val right: ConfiguredLength, val top: ConfiguredLength, val bottom: ConfiguredLength)
    class Paddings(val left: ConfiguredLength, val right: ConfiguredLength, val top: ConfiguredLength, val bottom: ConfiguredLength)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}