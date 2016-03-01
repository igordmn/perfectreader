package com.dmi.perfectreader.layout.liner

import java.util.*

interface Liner {
    fun makeLines(measuredText: MeasuredText, config: Config): List<Line>

    interface Line {
        val left: Float
        val width: Float
        val hasHyphenAfter: Boolean
        val isLast: Boolean
        val tokens: List<Token>
        val right: Float
            get() = left + width
    }

    interface Token {
        val isSpace: Boolean
        val beginIndex: Int
        val endIndex: Int
    }

    interface MeasuredText {
        val plainText: CharSequence
        val locale: Locale
        fun widthOf(index: Int): Float
        fun widthOf(beginIndex: Int, endIndex: Int): Float
        fun hyphenWidthAfter(index: Int): Float
    }

    interface Config {
        val firstLineIndent: Float
        val maxWidth: Float
        fun leftHangFactor(ch: Char): Float
        fun rightHangFactor(ch: Char): Float
    }
}
