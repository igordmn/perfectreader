package com.dmi.perfectreader.layout.wordbreak

import timber.log.Timber
import java.io.IOException
import java.util.*

class TeXWordBreaker(
        private val patternsSource: TeXPatternsSource
) : WordBreaker {
    private val hyphenatorCache = HyphenatorCache()

    override fun breakWord(text: CharSequence, locale: Locale, beginIndex: Int, endIndex: Int): WordBreaker.WordBreaks {
        return hyphenatorCache.getFor(locale).breakWord(text, beginIndex, endIndex)
    }

    private fun loadHyphenatorFor(locale: Locale): TeXHyphenator {
        val builder = TeXHyphenator.Builder()

        try {
            patternsSource.readPatternsFor(locale) {
                builder.addPatternsFrom(it)
            }
            patternsSource.readExceptionsFor(locale) {
                builder.addExceptionsFrom(it)
            }
        } catch (e: IOException) {
            Timber.i(e, "Cannot load hyphenation patterns for lang: %s", locale)
        }

        return builder.build()
    }

    private inner class HyphenatorCache {
        private var currentHyphenator: TeXHyphenator? = null
        private var previewHyphenator: TeXHyphenator? = null
        private var currentLocale: Locale? = null
        private var previewLocale: Locale? = null

        fun getFor(locale: Locale): TeXHyphenator {
            if (locale === previewLocale) {
                val temp = previewHyphenator
                previewHyphenator = currentHyphenator
                currentHyphenator = temp
                previewLocale = currentLocale
                currentLocale = locale
            } else if (locale !== currentLocale) {
                previewHyphenator = currentHyphenator
                currentHyphenator = loadHyphenatorFor(locale)
                previewLocale = currentLocale
                currentLocale = locale
            }

            return currentHyphenator!!
        }
    }
}
