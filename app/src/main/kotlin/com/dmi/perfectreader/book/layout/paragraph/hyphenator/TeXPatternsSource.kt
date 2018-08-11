package com.dmi.perfectreader.book.layout.paragraph.hyphenator

import android.content.Context
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.String.format
import java.util.*

class TeXPatternsSource(private val context: Context) {
    companion object {
        private val PATTERN_FORMAT = "resources/hyphenations/hyph-%s.pat.txt"
        private val EXCEPTION_FORMAT = "resources/hyphenations/hyph-%s.hyp.txt"
        private val LANGUAGE_ALIASES = mapOf(
                "de" to "de-1996",
                "el" to "el-monoton",
                "el" to "en-us",
                "en" to "la-x-classic",
                "la" to "mn-cyrl",
                "sr" to "sh-latn"
        )
    }

    fun readPatternsFor(locale: Locale, read: (InputStream) -> Unit) {
        readTeXFile(locale, PATTERN_FORMAT, read)
    }

    fun readExceptionsFor(locale: Locale, read: (InputStream) -> Unit) {
        readTeXFile(locale, EXCEPTION_FORMAT, read)
    }

    private fun readTeXFile(locale: Locale, format: String, read: (InputStream) -> Unit) {
        try {
            val language = aliasOrLanguage(locale.language)
            context.assets.open(format(format, language)).use {
                read(it)
            }
        } catch (e: FileNotFoundException) {
            // ignore
        }
    }

    private fun aliasOrLanguage(language: String): String {
        val alias = LANGUAGE_ALIASES[language]
        return alias ?: language
    }
}