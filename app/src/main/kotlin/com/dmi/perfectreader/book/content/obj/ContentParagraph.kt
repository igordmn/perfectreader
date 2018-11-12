package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredParagraph
import com.dmi.perfectreader.book.content.configure.common.ConfiguredFontStyle
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.location.textIndexAt
import com.dmi.perfectreader.book.content.location.textSubLocation
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentCompositeClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.perfectreader.book.content.obj.common.ContentStyle
import com.dmi.util.cache.Cache
import com.dmi.util.lang.extraCache
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI

class ContentParagraph(
        val runs: List<Run>,
        private val cls: ContentCompositeClass?,
        private val locale: Locale?
) : ContentObject {
    init {
        require(runs.isNotEmpty())
    }

    override val length = runs.sumByDouble { it.length }
    override val range = LocationRange(runs.first().range.start, runs.last().range.endInclusive)

    override fun configure(config: ContentConfig): ConfiguredParagraph {
        val style = config.style(cls)
        val firstLineIndentDip = style.textSizeDip * style.firstLineIndentEm

        return ConfiguredParagraph(
                config.locale(declared = locale),
                runs.map { it.configure(config) },
                firstLineIndentDip * config.density,
                style.textAlign,
                style.hyphenation,
                style.hangingConfig,
                range
        )
    }

    override fun toString() = runs.joinToString()

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

            override fun toString()= "[$obj]"
        }

        class Text(
                val text: String,
                private val cls: ContentCompositeClass?,
                override val range: LocationRange
        ) : Run() {
            init {
                require(text.isNotEmpty())
            }

            override val length = text.length.toDouble()

            override fun configure(config: ContentConfig): ConfiguredParagraph.Run.Text {
                val style = config.style(cls)
                return ConfiguredParagraph.Run.Text(
                        text,
                        config.configuredFontStyle[style],
                        style.lineHeightMultiplier,
                        range
                )
            }

            fun charLocation(index: Int) = textSubLocation(text, range, index)
            fun charIndex(location: Location) = textIndexAt(text, range, location)

            override fun toString() = text
        }
    }

    data class Builder(
            private val runs: ArrayList<ContentParagraph.Run> = ArrayList(),
            private val cls: ContentCompositeClass?,
            private val locale: Locale?
    ) {
        fun text(text: String, range: LocationRange) {
            run(ContentParagraph.Run.Text(text, cls, range))
        }

        fun obj(obj: ContentObject?) {
            if (obj != null)
                run(ContentParagraph.Run.Object(obj))
        }

        private fun run(run: ContentParagraph.Run) {
            runs.add(run)
        }

        fun customized(cls: ContentClass? = null, apply: Builder.() -> Unit) {
            val builder = if (cls == null) {
                this
            } else {
                val newCls = ContentCompositeClass(this.cls, cls)
                copy(cls = newCls)
            }
            builder.apply()
        }

        fun build() = if (runs.isNotEmpty()) ContentParagraph(runs, cls, locale) else null
    }
}

val ContentConfig.configuredFontStyle: Cache<ContentStyle, ConfiguredFontStyle> by extraCache(ContentConfig::computeConfiguredFontStyle)

fun ContentConfig.computeConfiguredFontStyle(style: ContentStyle): ConfiguredFontStyle {
    val textSize = style.textSizeDip * density

    return ConfiguredFontStyle(
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