package com.dmi.perfectreader.book.parse

import com.dmi.perfectreader.settings.Settings

class ParseConfig(
        val defaultCharset: Charset,
        val ignoreDeclaredCharset: Boolean
) {
    sealed class Charset {
        object Auto : Charset()
        class Fixed(val name: String) : Charset()
    }
}

fun settingsParseConfig(settings: Settings) = ParseConfig(
        defaultCharset(settings),
        settings.analyze.ignoreDeclaredCharset
)

private fun defaultCharset(settings: Settings) = if (settings.analyze.defaultCharsetIsAuto) {
    ParseConfig.Charset.Auto
} else {
    ParseConfig.Charset.Fixed(settings.analyze.defaultCharset)
}