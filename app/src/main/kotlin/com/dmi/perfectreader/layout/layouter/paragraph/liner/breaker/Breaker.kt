package com.dmi.perfectreader.layout.layouter.paragraph.liner.breaker

import java.util.*

interface Breaker {
    fun breakText(text: String, locale: Locale): Breaks
}
