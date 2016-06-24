package com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator

import java.util.*

interface HyphenatorResolver {
    fun hyphenatorFor(locale: Locale): Hyphenator
}