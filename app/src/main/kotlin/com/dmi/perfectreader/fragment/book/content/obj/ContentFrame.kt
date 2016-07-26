package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.Length
import com.dmi.perfectreader.fragment.book.content.obj.param.StyleType
import com.dmi.perfectreader.fragment.book.location.LocationRange
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

    override fun configure(config: LayoutConfig) = ConfiguredFrame(
            margins.configure(config, styleType),
            paddings.configure(config),
            borders.configure(config),
            background.configure(),
            child.configure(config),
            range
    )

    class Background(val color: Color?) {
        fun configure() = ConfiguredFrame.Background(color ?: Color.TRANSPARENT)
    }

    class Margins(val left: Length?, val right: Length?, val top: Length?, val bottom: Length?) {
        fun configure(config: LayoutConfig, styleType: StyleType): ConfiguredFrame.Margins {
            return if (styleType == StyleType.PARAGRAPH) {
                val top = (top ?: Length.Absolute(DEFAULT_PARAGRAPH_VERTICAL_MARGIN)).configure(config)
                val bottom = (bottom ?: Length.Absolute(DEFAULT_PARAGRAPH_VERTICAL_MARGIN)).configure(config)
                ConfiguredFrame.Margins(
                        (left ?: Length.Absolute(0F)).configure(config),
                        (right ?: Length.Absolute(0F)).configure(config),
                        Length.Multiplier(top, config.paragraphVerticalMarginMultiplier),
                        Length.Multiplier(bottom, config.paragraphVerticalMarginMultiplier)
                )
            } else {
                ConfiguredFrame.Margins(
                        (left ?: Length.Absolute(0F)).configure(config),
                        (right ?: Length.Absolute(0F)).configure(config),
                        (top ?: Length.Absolute(0F)).configure(config),
                        (bottom ?: Length.Absolute(0F)).configure(config)
                )
            }
        }
    }

    class Paddings(val left: Length?, val right: Length?, val top: Length?, val bottom: Length?) {
        fun configure(config: LayoutConfig) = ConfiguredFrame.Paddings(
                (left ?: Length.Absolute(0F)).configure(config),
                (right ?: Length.Absolute(0F)).configure(config),
                (top ?: Length.Absolute(0F)).configure(config),
                (bottom ?: Length.Absolute(0F)).configure(config)
        )
    }

    class Border(val width: Float?, val color: Color?) {
        fun configure(config: LayoutConfig) = ConfiguredFrame.Border(
                (width ?: 0F) * config.density,
                color ?: Color.BLACK
        )
    }

    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border) {
        fun configure(config: LayoutConfig) = ConfiguredFrame.Borders(
                left.configure(config),
                right.configure(config),
                top.configure(config),
                bottom.configure(config)
        )
    }
}

class ConfiguredFrame(
        val margins: Margins,
        val paddings: Paddings,
        val borders: Borders,
        val background: Background,
        val child: ConfiguredObject,
        range: LocationRange
) : ConfiguredObject(range) {
    class Background(val color: Color)
    class Margins(val left: Length, val right: Length, val top: Length, val bottom: Length)
    class Paddings(val left: Length, val right: Length, val top: Length, val bottom: Length)
    class Border(val width: Float, val color: Color)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}