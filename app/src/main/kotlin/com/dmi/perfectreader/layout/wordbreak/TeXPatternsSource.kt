package com.dmi.perfectreader.layout.wordbreak

import android.content.Context
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.String.format
import java.util.*

class TeXPatternsSource(private val context: Context) {
    companion object {
        private val patternFormat = "hyphenation/hyph-%s.pat.txt"
        private val exceptionFormat = "hyphenation/hyph-%s.hyp.txt"

        private val languageAliases = object : HashMap<String, String>() {
            init {
                put("de", "de-1996")
                put("el", "el-monoton")
                put("en", "en-us")
                put("la", "la-x-classic")
                put("mn", "mn-cyrl")
                put("sr", "sh-latn")
            }
        }
    }

    @Throws(IOException::class)
    fun readPatternsFor(locale: Locale): InputStream? {
        return readTeXFile(locale, patternFormat)
    }

    @Throws(IOException::class)
    fun readExceptionsFor(locale: Locale): InputStream? {
        return readTeXFile(locale, exceptionFormat)
    }

    @Throws(IOException::class)
    private fun readTeXFile(locale: Locale, format: String): InputStream? {
        try {
            val language = aliasOrLanguage(locale.language)
            return context.assets.open(format(format, language))
        } catch (e: FileNotFoundException) {
            return null
        }

    }

    private fun aliasOrLanguage(language: String): String {
        val alias = languageAliases[language]
        return alias ?: language
    }
}
