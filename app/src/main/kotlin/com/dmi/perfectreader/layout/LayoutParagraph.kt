package com.dmi.perfectreader.layout

import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import java.util.*

class LayoutParagraph(
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hangingConfig: HangingConfig
) : LayoutObject() {
    sealed class Run {
        class Object(val obj: LayoutObject) : Run()
        class Text(val text: String, val style: FontStyle) : Run()
    }

    interface HangingConfig {
        fun leftHangFactor(ch: Char): Float
        fun rightHangFactor(ch: Char): Float
    }
}
