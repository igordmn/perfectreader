package com.dmi.perfectreader.book.layout.paragraph.hyphenator

import com.dmi.util.log.Log
import java.io.IOException
import java.util.*

class TeXHyphenatorResolver(
        private val log: Log,
        private val patternsSource: TeXPatternsSource
) : HyphenatorResolver {
    override fun hyphenatorFor(locale: Locale): TeXHyphenator {
        val builder = TeXHyphenator.Builder()

        try {
            patternsSource.readPatternsFor(locale) {
                builder.addPatternsFrom(it)
            }
            patternsSource.readExceptionsFor(locale) {
                builder.addExceptionsFrom(it)
            }
        } catch (e: IOException) {
            log.i(e, "Cannot load hyphenation patterns for lang: $locale")
        }

        return builder.build()
    }
}