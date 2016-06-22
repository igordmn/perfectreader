package com.dmi.perfectreader.fragment.book.parse

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.util.setting.Settings

class ParseConfig(
        val defaultCharset: Charset,
        val ignoreDeclaredCharset: Boolean
) {
    sealed class Charset {
        companion object {
            fun parse(str: String) = when (str) {
                "auto" -> Charset.Auto
                else -> Fixed(str)
            }
        }

        object Auto: Charset()
        class Fixed(val name: String): Charset()
    }
}

fun settingsParseConfig(userSettings: Settings) = ParseConfig(
        ParseConfig.Charset.parse(userSettings[UserSettingKeys.Analyze.defaultCharset]),
        userSettings[UserSettingKeys.Analyze.ignoreDeclaredCharset]
)