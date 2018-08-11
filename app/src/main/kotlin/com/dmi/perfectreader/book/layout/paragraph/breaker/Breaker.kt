package com.dmi.perfectreader.book.layout.paragraph.breaker

import java.util.*

interface Breaker {
    fun breakText(text: String, locale: Locale, config: Config): Breaks

    class Config(val hyphenation: Boolean)
}