package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredObject
import com.dmi.perfectreader.book.content.configure.ConfiguredParagraph
import com.dmi.perfectreader.book.content.configure.common.ConfiguredFontStyle
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.location.textIndexAt
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.util.lang.extra
import java.util.*
import kotlin.math.PI

class ContentParagraph(
        val locale: Locale?,
        val runs: List<Run>,
        private val cls: ContentClass?,
        override val range: LocationRange
) : ContentObject {
    override val length = runs.sumByDouble { it.length }

    init {
        require(runs.isNotEmpty())
    }

    override fun configure(config: ContentConfig): ConfiguredObject {
        val styled = config.styled[cls]
        val style = styled.style
        val inherited = styled.inherited

        val firstLineIndentDip = style.textSizeDip * style.firstLineIndentEm

        return ConfiguredParagraph(
                if (styled.ignoreDeclaredLocale) styled.defaultLocale else locale ?: styled.defaultLocale,
                runs.map { it.configure(inherited) },
                firstLineIndentDip * styled.density,
                style.textAlign,
                style.hyphenation,
                style.hangingConfig,
                range
        )
    }

    sealed class Run {
        abstract val range: LocationRange
        abstract val length: Double

        abstract fun configure(config: ContentConfig): ConfiguredParagraph.Run

        class Object(val obj: ContentObject) : Run() {
            override val length = obj.length
            override val range = obj.range

            override fun configure(config: ContentConfig) = ConfiguredParagraph.Run.Object(
                    obj.configure(config),
                    lineHeightMultiplier = 1F
            )
        }

        class Text(
                val text: String,
                private val cls: ContentClass?,
                override val range: LocationRange
        ) : Run() {
            init {
                require(text.isNotEmpty())
            }

            override val length = text.length.toDouble()

            override fun configure(config: ContentConfig): ConfiguredParagraph.Run.Text {
                val styled = config.styled[cls]
                return ConfiguredParagraph.Run.Text(
                        text,
                        styled.configuredFontStyle,
                        styled.style.lineHeightMultiplier,
                        range
                )
            }

            fun charIndex(location: Location) = textIndexAt(text, range, location)
        }
    }
}

val ContentConfig.configuredFontStyle: ConfiguredFontStyle by extra {
    val style = style
    val textSize = style.textSizeDip * density

    ConfiguredFontStyle(
            fonts.loadFont(style.textFontFamily, style.textFontIsBold, style.textFontIsItalic),

            textSize,
            style.letterSpacingEm * textSize,
            style.wordSpacingMultiplier,
            style.textScaleX,
            style.textSkewX,
            style.textStrokeWidthDip * density,
            style.textColor,
            style.textAntialiasing,
            style.textHinting,
            style.textSubpixelPositioning,

            style.textShadowEnabled,
            (style.textShadowAngleDegrees * PI / 180).toFloat(),
            style.textShadowOffsetEm * textSize,
            style.textShadowSizeEm * textSize,
            style.textShadowBlurEm * textSize,
            style.textShadowColor,

            style.selectionColor
    )
}