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
    class Background(val color: Color)  {
        companion object {
            val Transparent = ConfiguredFrame.Background(Color.TRANSPARENT)
        }
    }

    class Margins(val left: ConfiguredLength, val right: ConfiguredLength, val top: ConfiguredLength, val bottom: ConfiguredLength) {
        companion object {
            val Zero = ConfiguredFrame.Margins(
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero
            )
        }
    }

    class Paddings(val left: ConfiguredLength, val right: ConfiguredLength, val top: ConfiguredLength, val bottom: ConfiguredLength) {
        companion object {
            val Zero = ConfiguredFrame.Paddings(
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero,
                    ConfiguredLength.Zero
            )
        }
    }

    class Border(val width: Float, val color: Color) {
        companion object {
            val Zero = Border(0F, Color.TRANSPARENT)
        }
    }

    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border) {
        companion object {
            val Zero = ConfiguredFrame.Borders(
                    Border.Zero,
                    Border.Zero,
                    Border.Zero,
                    Border.Zero
            )
        }
    }
}