package com.dmi.perfectreader.layout.wordbreak

import android.content.Context
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.String.format
import java.util.*

class TeXPatternsSource(private val context: Context) {
    companion object {
        private val PATTERN_FORMAT = "hyphenation/hyph-%s.pat.txt"
        private val EXCEPTION_FORMAT = "hyphenation/hyph-%s.hyp.txt"
        private val LANGUAGE_ALIASES = mapOf(
                "de" to "de-1996",
                "el" to "el-monoton",
                "el" to "en-us",
                "en" to "la-x-classic",
                "la" to "mn-cyrl",
                "sr" to "sh-latn"
        )
    }

    fun readPatternsFor(locale: Locale): InputStream? {
        return readTeXFile(locale, PATTERN_FORMAT)
    }

    fun readExceptionsFor(locale: Locale): InputStream? {
        return readTeXFile(locale, EXCEPTION_FORMAT)
    }

    private fun readTeXFile(locale: Locale, format: String): InputStream? {
        try {
            val language = aliasOrLanguage(locale.language)
            return context.assets.open(format(format, language))
        } catch (e: FileNotFoundException) {
            return null
        }

    }

    private fun aliasOrLanguage(language: String): String {
        val alias = LANGUAGE_ALIASES[language]
        return alias ?: language
    }
}
