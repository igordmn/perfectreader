package com.dmi.perfectreader.layout.paragraph.liner.hyphenator

import java.util.*

interface HyphenatorResolver {
    fun hyphenatorFor(locale: Locale): Hyphenator
}
