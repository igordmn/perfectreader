package com.dmi.perfectreader.fragment.book.obj.content

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import com.dmi.perfectreader.fragment.book.obj.common.TextAlign
import com.dmi.perfectreader.fragment.book.obj.content.param.ContentFontStyle
import com.dmi.perfectreader.fragment.book.obj.content.param.StyleType
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutParagraph
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
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

    override fun configure(config: LayoutConfig) = LayoutParagraph(
            if (config.ignoreDeclaredLocale) config.defaultLocale else locale ?: config.defaultLocale,
            runs.map { it.configure(config) },
            firstLineIndent ?: config.firstLineIndent,
            textAlign ?: config.textAlign,
            config.hyphenation,
            config.hangingConfig, range
    )

    sealed class Run {
        abstract val length: Double
        abstract fun configure(config: LayoutConfig): LayoutParagraph.Run

        class Object(val obj: ContentObject) : Run() {
            override val length = obj.length

            override fun configure(config: LayoutConfig) = LayoutParagraph.Run.Object(
                    obj.configure(config)
            )
        }

        class Text(val text: String, val style: ContentFontStyle, val range: LocationRange) : Run() {
            override val length = text.length.toDouble()

            override fun configure(config: LayoutConfig) = LayoutParagraph.Run.Text(
                    text, style.configure(config), range
            )
        }
    }
}

private fun ContentFontStyle.configure(config: LayoutConfig) = LayoutFontStyle(
        (size ?: ContentParagraph.DEFAULT_FONT_SIZE) * config.fontSizeMultiplier,
        color ?: Color.BLACK,
        config.textRenderConfig,
        config.selectionConfig
)