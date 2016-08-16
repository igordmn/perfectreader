package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.*
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.location.textSubLocation
import com.dmi.perfectreader.fragment.book.location.textSubRange
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

    init {
        require(runs.size > 0)
    }

    override fun configure(config: ContentConfig) = ConfiguredParagraph(
            if (config.ignoreDeclaredLocale) config.defaultLocale else locale ?: config.defaultLocale,
            runs.map { it.configure(config) },
            (firstLineIndent ?: config.firstLineIndent) * config.density,
            textAlign ?: config.textAlign,
            config.hyphenation,
            config.hangingConfig,
            range
    )

    sealed class Run {
        abstract val range: LocationRange
        abstract val length: Double
        abstract fun configure(config: ContentConfig): ConfiguredParagraph.Run

        class Object(val obj: ContentObject) : Run() {
            override val length = obj.length
            override val range = obj.range

            override fun configure(config: ContentConfig) = ConfiguredParagraph.Run.Object(
                    obj.configure(config)
            )
        }

        class Text(val text: String, val style: ContentFontStyle, override val range: LocationRange) : Run() {
            init {
                require(text.length > 0)
            }

            override val length = text.length.toDouble()

            override fun configure(config: ContentConfig) = ConfiguredParagraph.Run.Text(
                    text, fontStyleCache.configure(style, config), range
            )

            fun charRange(beginIndex: Int, endIndex: Int) = textSubRange(text, range, beginIndex, endIndex)
            fun charLocation(index: Int) = textSubLocation(text, range, index)
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
            fun charRange(beginIndex: Int, endIndex: Int) = textSubRange(text, range, beginIndex, endIndex)
            fun charLocation(index: Int) = textSubLocation(text, range, index)
        }
    }
}

private class FontStyleCache {
    private val lastConfigured = WeakHashMap<ContentFontStyle, ConfiguredFontStyle>()
    private val lastConfigs = WeakHashMap<ContentFontStyle, ContentConfig>()

    fun configure(style: ContentFontStyle, config: ContentConfig): ConfiguredFontStyle {
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

private fun ContentFontStyle.configure(config: ContentConfig) = ConfiguredFontStyle(
        (size ?: ContentParagraph.DEFAULT_FONT_SIZE) * config.density * config.fontSizeMultiplier,
        color ?: Color.BLACK,
        config.textRenderConfig,
        config.selectionConfig
)