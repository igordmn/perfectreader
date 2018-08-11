package com.dmi.perfectreader.book.layout.paragraph.hyphenator

import java.util.*

interface HyphenatorResolver {
    fun hyphenatorFor(locale: Locale): Hyphenator
}