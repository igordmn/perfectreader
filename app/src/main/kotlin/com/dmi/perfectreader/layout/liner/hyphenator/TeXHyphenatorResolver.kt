package com.dmi.perfectreader.layout.liner.hyphenator

import timber.log.Timber
import java.io.IOException
import java.util.*

class TeXHyphenatorResolver(private val patternsSource: TeXPatternsSource) : HyphenatorResolver {
    private var currentHyphenator: TeXHyphenator? = null
    private var previewHyphenator: TeXHyphenator? = null
    private var currentLocale: Locale? = null
    private var previewLocale: Locale? = null

    override fun hyphenatorFor(locale: Locale): TeXHyphenator {
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
}
