package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.content.obj.param.HangingConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.TextAlign
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.StyleType
import com.dmi.util.graphic.Color
import java.util.*

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

    override fun configure(config: LayoutConfig) = ComputedParagraph(
            if (config.ignoreDeclaredLocale) config.defaultLocale else locale ?: config.defaultLocale,
            runs.map { it.configure(config) },
            firstLineIndent ?: config.firstLineIndent,
            textAlign ?: config.textAlign,
            config.hyphenation,
            config.hangingConfig, range
    )

    sealed class Run {
        abstract val length: Double
        abstract fun configure(config: LayoutConfig): ComputedParagraph.Run

        class Object(val obj: ContentObject) : Run() {
            override val length = obj.length

            override fun configure(config: LayoutConfig) = ComputedParagraph.Run.Object(
                    obj.configure(config)
            )
        }

        class Text(val text: String, val style: ContentFontStyle, val range: LocationRange) : Run() {
            override val length = text.length.toDouble()

            override fun configure(config: LayoutConfig) = ComputedParagraph.Run.Text(
                    text, style.configure(config), range
            )

            private fun ContentFontStyle.configure(config: LayoutConfig) = ComputedFontStyle(
                    (size ?: DEFAULT_FONT_SIZE) * config.fontSizeMultiplier,
                    color ?: Color.BLACK,
                    config.textRenderConfig,
                    config.selectionConfig
            )
        }
    }
}

class ComputedParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hyphenation: Boolean,
        val hangingConfig: HangingConfig,
        range: LocationRange
) : ComputedObject(range) {
    sealed class Run {
        class Object(val obj: ComputedObject) : Run()
        class Text(val text: String, val style: ComputedFontStyle, val range: LocationRange) : Run() {
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