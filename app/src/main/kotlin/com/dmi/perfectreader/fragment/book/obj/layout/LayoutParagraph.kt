package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.HangingConfig
import com.dmi.perfectreader.fragment.book.obj.common.TextAlign
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import java.util.*

class LayoutParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hyphenation: Boolean,
        val hangingConfig: HangingConfig,
        range: LocationRange
) : LayoutObject(range) {
    sealed class Run {
        class Object(val obj: LayoutObject) : Run()
        class Text(val text: String, val style: LayoutFontStyle, val range: LocationRange) : Run() {
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