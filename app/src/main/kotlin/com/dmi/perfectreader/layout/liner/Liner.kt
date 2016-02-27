package com.dmi.perfectreader.layout.liner

import java.util.*

interface Liner {
    fun makeLines(measuredText: MeasuredText, config: Config): List<Line>

    interface Line {
        fun left(): Float
        fun width(): Float
        fun hasHyphenAfter(): Boolean
        fun isLast(): Boolean
        fun tokens(): List<Token>
    }

    interface Token {
        fun isSpace(): Boolean
        fun beginIndex(): Int
        fun endIndex(): Int
    }

    interface MeasuredText {
        fun plainText(): CharSequence
        fun locale(): Locale
        fun widthOf(index: Int): Float
        fun widthOf(beginIndex: Int, endIndex: Int): Float
        fun hyphenWidthAfter(index: Int): Float
    }

    interface Config {
        fun firstLineIndent(): Float
        fun maxWidth(): Float
        fun leftHangFactor(ch: Char): Float
        fun rightHangFactor(ch: Char): Float
    }
}
