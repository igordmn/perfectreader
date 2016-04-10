package com.dmi.perfectreader.layout.layoutobj

import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.layout.layoutobj.common.FontStyle
import com.dmi.perfectreader.layout.layoutobj.common.TextAlign
import java.util.*

class LayoutParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hangingConfig: HangingConfig,
        range: BookRange
) : LayoutObject(range) {
    sealed class Run {
        class Object(val obj: LayoutObject) : Run()
        class Text(val text: String, val style: FontStyle, val range: BookRange) : Run() {
            fun subrange(beginIndex: Int, endIndex: Int) = range.subrange(
                    beginIndex.toDouble() / text.length,
                    endIndex.toDouble() / text.length
            )
        }
    }

    interface HangingConfig {
        fun leftHangFactor(ch: Char): Float
        fun rightHangFactor(ch: Char): Float
    }
}
