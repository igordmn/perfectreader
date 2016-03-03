package com.dmi.perfectreader.layout.liner.hyphenator

import java.util.*

interface HyphenatorResolver {
    fun hyphenatorFor(locale: Locale): Hyphenator
}
