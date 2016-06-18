package com.dmi.perfectreader.fragment.book.obj.content

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import com.dmi.perfectreader.fragment.book.obj.common.Length
import com.dmi.perfectreader.fragment.book.obj.content.param.StyleType
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutFrame
import com.dmi.util.graphic.Color

class ContentFrame(
        val styleType: StyleType,
        val margins: Margins,
        val paddings: Paddings,
        val borders: Borders,
        val background: Background,
        val child: ContentObject,
        range: LocationRange
) : ContentObject(range) {
    companion object {
        private val DEFAULT_PARAGRAPH_VERTICAL_MARGIN = 8F
    }

    override val length = child.length

    override fun configure(config: LayoutConfig) = LayoutFrame(
            margins.configure(config, styleType),
            paddings.configure(),
            borders.configure(),
            background.configure(),
            child.configure(config),
            range
    )

    class Background(val color: Color?) {
        fun configure() = LayoutFrame.Background(color ?: Color.TRANSPARENT)
    }

    class Margins(val left: Length?, val right: Length?, val top: Length?, val bottom: Length?) {
        fun configure(config: LayoutConfig, styleType: StyleType): LayoutFrame.Margins {
            return if (styleType == StyleType.PARAGRAPH) {
                val top = top ?: Length.Absolute(DEFAULT_PARAGRAPH_VERTICAL_MARGIN)
                val bottom = bottom ?: Length.Absolute(DEFAULT_PARAGRAPH_VERTICAL_MARGIN)
                LayoutFrame.Margins(
                        left ?: Length.Absolute(0F),
                        right ?: Length.Absolute(0F),
                        Length.Multiplier(top, config.paragraphVerticalMarginMultiplier),
                        Length.Multiplier(bottom, config.paragraphVerticalMarginMultiplier)
                )
            } else {
                LayoutFrame.Margins(
                        left ?: Length.Absolute(0F),
                        right ?: Length.Absolute(0F),
                        top ?: Length.Absolute(0F),
                        bottom ?: Length.Absolute(0F)
                )
            }
        }
    }

    class Paddings(val left: Length?, val right: Length?, val top: Length?, val bottom: Length?) {
        fun configure() = LayoutFrame.Paddings(
                left ?: Length.Absolute(0F),
                right ?: Length.Absolute(0F),
                top ?: Length.Absolute(0F),
                bottom ?: Length.Absolute(0F)
        )
    }

    class Border(val width: Float?, val color: Color?) {
        fun configure() = LayoutFrame.Border(
                width ?: 0F,
                color ?: Color.BLACK
        )
    }

    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border) {
        fun configure() = LayoutFrame.Borders(
                left.configure(),
                right.configure(),
                top.configure(),
                bottom.configure()
        )
    }
}