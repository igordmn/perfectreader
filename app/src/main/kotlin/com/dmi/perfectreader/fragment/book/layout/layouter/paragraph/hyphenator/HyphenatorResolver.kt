package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator

import java.util.*

interface HyphenatorResolver {
    fun hyphenatorFor(locale: Locale): Hyphenator
}