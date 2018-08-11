package com.dmi.perfectreader.book.parse

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.util.setting.Settings
import com.dmi.perfectreader.data.UserSettingKeys.Analyze as AnalyzeKeys

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
        settings[UserSettingKeys.Analyze.ignoreDeclaredCharset]
)

private fun defaultCharset(settings: Settings) = if (settings[AnalyzeKeys.defaultCharsetIsAuto]) {
    ParseConfig.Charset.Auto
} else {
    ParseConfig.Charset.Fixed(settings[AnalyzeKeys.defaultCharset])
}