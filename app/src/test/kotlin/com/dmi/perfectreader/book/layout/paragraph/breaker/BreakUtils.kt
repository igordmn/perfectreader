package com.dmi.perfectreader.book.layout.paragraph.breaker

import java.util.*

fun breakIndicesOf(breaks: Breaks, text: String) = ArrayList<Int>().apply {
    for (i in 0 until text.length) {
        if (breaks.hasBreakBefore(i))
            add(i)
    }
}

fun hyphenIndicesOf(breaks: Breaks, text: String) = ArrayList<Int>().apply {
    for (i in 0 until text.length) {
        if (breaks.hasHyphenBefore(i))
            add(i)
    }
}