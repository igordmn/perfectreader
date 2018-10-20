package com.dmi.perfectreader.book.content.configure

import com.dmi.perfectreader.book.content.common.HangingConfig
import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.perfectreader.book.content.configure.common.ConfiguredFontStyle
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.location.textSubLocation
import com.dmi.perfectreader.book.content.location.textSubRange
import java.util.*

class ConfiguredParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hyphenation: Boolean,
        val hangingConfig: HangingConfig,
        override val range: LocationRange
) : ConfiguredObject {
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