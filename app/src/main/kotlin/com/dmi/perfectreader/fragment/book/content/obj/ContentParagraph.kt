package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.*
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.graphic.Color
import java.util.*

private val fontStyleCache = FontStyleCache()

class ContentParagraph(
        val styleType: StyleType,
        val locale: Locale?,
        val runs: List<Run>,
        val firstLineIndent: Float?,
        val textAlign: TextAlign?,
        range: LocationRange
) : ContentObject(range) {
    override val length = runs.sumByDouble { it.length }

    companion object {
        val DEFAULT_FONT_SIZE = 20F
    }

    override fun configure(config: LayoutConfig) = ConfiguredParagraph(
            if (config.ignoreDeclaredLocale) config.defaultLocale else locale ?: config.defaultLocale,
            runs.map { it.configure(config) },
            firstLineIndent ?: config.firstLineIndent,
            textAlign ?: config.textAlign,
            config.hyphenation,
            config.hangingConfig, range
    )

    sealed class Run {
        abstract val length: Double
        abstract fun configure(config: LayoutConfig): ConfiguredParagraph.Run

        class Object(val obj: ContentObject) : Run() {
            override val length = obj.length

            override fun configure(config: LayoutConfig) = ConfiguredParagraph.Run.Object(
                    obj.configure(config)
            )
        }

        class Text(val text: String, val style: ContentFontStyle, val range: LocationRange) : Run() {
            override val length = text.length.toDouble()

            override fun configure(config: LayoutConfig) = ConfiguredParagraph.Run.Text(
                    text, fontStyleCache.configure(style, config), range
            )
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
    sealed class Run {
        class Object(val obj: ConfiguredObject) : Run()
        class Text(val text: String, val style: ConfiguredFontStyle, val range: LocationRange) : Run() {
            fun subrange(beginIndex: Int, endIndex: Int) = range.subrange(
                    beginIndex.toDouble() / text.length,
                    endIndex.toDouble() / text.length
            )

            fun sublocation(index: Int) = range.sublocation(
                    index.toDouble() / text.length
            )
        }
    }
}

private class FontStyleCache {
    private val lastConfigured = WeakHashMap<ContentFontStyle, ConfiguredFontStyle>()
    private val lastConfigs = WeakHashMap<ContentFontStyle, LayoutConfig>()

    fun configure(style: ContentFontStyle, config: LayoutConfig): ConfiguredFontStyle {
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

private fun ContentFontStyle.configure(config: LayoutConfig) = ConfiguredFontStyle(
        (size ?: ContentParagraph.DEFAULT_FONT_SIZE) * config.fontSizeMultiplier,
        color ?: Color.BLACK,
        config.textRenderConfig,
        config.selectionConfig
)