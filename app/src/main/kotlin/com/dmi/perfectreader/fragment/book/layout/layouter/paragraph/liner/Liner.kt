package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.liner

import java.util.*

interface Liner {
    fun makeLines(measuredText: MeasuredText, config: Config): List<Line>

    class Line {
        var left = 0F
        var width = 0F
        var hasHyphenAfter = false
        var tokens = ArrayList<Token>()
        val right: Float get() = left + width
    }

    class Token {
        var isSpace = false
        var beginIndex = 0
        var endIndex = 0
    }

    interface MeasuredText {
        val plainText: String
        val locale: Locale
        fun widthOf(index: Int): Float
        fun widthOf(beginIndex: Int, endIndex: Int): Float
        fun hyphenWidthAfter(index: Int): Float
    }

    interface Config {
        val maxWidth: Float
        val firstLineIndent: Float
        val hyphenation: Boolean
        fun leftHangFactor(ch: Char): Float
        fun rightHangFactor(ch: Char): Float
    }
}