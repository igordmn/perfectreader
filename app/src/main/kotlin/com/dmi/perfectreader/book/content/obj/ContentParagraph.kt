package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.location.*
import com.dmi.perfectreader.book.content.obj.param.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI

private val fontStyleCache = FontStyleCache()

class ContentParagraph(
        val styleType: StyleType,
        val locale: Locale?,
        val runs: List<Run>,
        val firstLineIndent: Float?,
        val textAlign: TextAlign?,
        textSize: Float?,
        range: LocationRange
) : ContentObject(range, textSize) {
    companion object {
        val DEFAULT_TEXT_SIZE_DIP = 16F
    }

    override val length = runs.sumByDouble { it.length }

    init {
        require(runs.isNotEmpty())
    }

    override fun configure(config: FormatConfig): ConfiguredParagraph {
        val firstLineIndentDip = emToDip(config.firstLineIndentEm, config) + (firstLineIndent ?: 0F)

        return ConfiguredParagraph(
                if (config.ignoreDeclaredLocale) config.defaultLocale else locale ?: config.defaultLocale,
                runs.map { it.configure(config) },
                firstLineIndentDip * config.density,
                textAlign ?: config.textAlign,
                config.hyphenation,
                config.hangingConfig,
                range
        )
    }

    sealed class Run(val lineHeightMultiplier: Float?) {
        abstract val range: LocationRange
        abstract val length: Double
        abstract fun configure(config: FormatConfig): ConfiguredParagraph.Run

        class Object(val obj: ContentObject, lineHeightMultiplier: Float?) : Run(lineHeightMultiplier) {
            override val length = obj.length
            override val range = obj.range

            override fun configure(config: FormatConfig) = ConfiguredParagraph.Run.Object(
                    obj.configure(config),
                    lineHeightMultiplier ?: config.lineHeightMultiplier
            )
        }

        class Text(
                val text: String,
                val style: ContentFontStyle,
                lineHeightMultiplier: Float?,
                override val range: LocationRange
        ) : Run(lineHeightMultiplier) {
            init {
                require(text.isNotEmpty())
            }

            override val length = text.length.toDouble()

            override fun configure(config: FormatConfig) = ConfiguredParagraph.Run.Text(
                    text,
                    fontStyleCache.configure(style, config),
                    lineHeightMultiplier ?: config.lineHeightMultiplier,
                    range
            )

            fun charRange(beginIndex: Int, endIndex: Int) = textSubRange(text, range, beginIndex, endIndex)
            fun charLocation(index: Int) = textSubLocation(text, range, index)
            fun charIndex(location: Location) = textIndexAt(text, range, location)
        }
    }
}

class ConfiguredParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hyphenation: Boolean,
        val hangingConfig: HangingConfig,
        range: LocationRange
) : ConfiguredObject(range) {
    sealed class Run(val lineHeightMultiplier: Float) {
        class Object(val obj: ConfiguredObject, lineHeightMultiplier: Float) : Run(lineHeightMultiplier)
        class Text(
                val text: String,
                val style: ConfiguredFontStyle,
                lineHeightMultiplier: Float,
                val range: LocationRange
        ) : Run(lineHeightMultiplier) {
            fun charRange(beginIndex: Int, endIndex: Int) = textSubRange(text, range, beginIndex, endIndex)
            fun charLocation(index: Int) = textSubLocation(text, range, index)
        }
    }
}

private class FontStyleCache {
    private val lastConfigured = WeakHashMap<ContentFontStyle, ConfiguredFontStyle>()
    private val lastConfigs = WeakHashMap<ContentFontStyle, FormatConfig>()

    fun configure(style: ContentFontStyle, config: FormatConfig): ConfiguredFontStyle {
        val lastConfigured = lastConfigured[style]
        val lastConfig = lastConfigs[style]
        if (lastConfigured != null && lastConfig === config) {
            return lastConfigured
        } else {
            val configured = style.configure(config)
            this.lastConfigured[style] = configured
            this.lastConfigs[style] = config
            return configured
        }
    }
}

private fun ContentFontStyle.configure(config: FormatConfig): ConfiguredFontStyle {
    val textSizeMultiplier = config.textSizeDip / ContentParagraph.DEFAULT_TEXT_SIZE_DIP
    val textSize = (size ?: ContentParagraph.DEFAULT_TEXT_SIZE_DIP) * textSizeMultiplier * config.density

    return ConfiguredFontStyle(
            config.fonts.loadFont(config.textFontFamily, config.textFontIsBold, config.textFontIsItalic),

            textSize,
            config.letterSpacingEm * textSize,
            config.wordSpacingMultiplier,
            config.textScaleX,
            config.textSkewX,
            config.textStrokeWidthDip * config.density,
            color ?: config.textColor,
            config.textAntialiasing,
            config.textHinting,
            config.textSubpixelPositioning,

            config.textShadowEnabled,
            (config.textShadowAngleDegrees * PI / 180).toFloat(),
            config.textShadowOffsetEm * textSize,
            config.textShadowSizeEm * textSize,
            config.textShadowBlurEm * textSize,
            config.textShadowColor,

            config.selectionColor
    )
}