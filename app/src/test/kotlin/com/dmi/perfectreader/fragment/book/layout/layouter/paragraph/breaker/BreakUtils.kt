package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker

import java.util.*

fun breakIndicesOf(breaks: Breaks, text: String) = ArrayList<Int>().apply {
    for (i in 0..text.length - 1) {
        if (breaks.hasBreakBefore(i))
            add(i)
    }
}

fun hyphenIndicesOf(breaks: Breaks, text: String) = ArrayList<Int>().apply {
    for (i in 0..text.length - 1) {
        if (breaks.hasHyphenBefore(i))
            add(i)
    }
}