package com.dmi.perfectreader.fragment.book.obj.common

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import java.util.*
import com.dmi.perfectreader.data.UserSettingKeys.Analyze as AnalyzeKeys
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class LayoutConfig(
        val defaultLocale: Locale,
        val ignoreDeclaredLocale: Boolean,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val fontSizeMultiplier: Float,
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginMultiplier: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,
        val textRenderParams: TextRenderParams
)

fun settingsLayoutConfig(context: Context, settings: UserSettings) = LayoutConfig(
        defaultLocale = parseLanguage(context, settings[AnalyzeKeys.defaultLanguage]),
        ignoreDeclaredLocale = settings[AnalyzeKeys.ignoreDeclaredLanguage],
        firstLineIndent = settings[FormatKeys.firstLineIndent],
        textAlign = settings[FormatKeys.textAlign],
        fontSizeMultiplier = settings[FormatKeys.fontSizeMultiplier],
        lineHeightMultiplier = settings[FormatKeys.lineHeightMultiplier],
        paragraphVerticalMarginMultiplier = settings[FormatKeys.paragraphVerticalMarginMultiplier],
        hangingConfig = if (settings[FormatKeys.hangingPunctuation]) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings[FormatKeys.hyphenation],
        textRenderParams = TextRenderParams(antialias = true, hinting = true, linearScaling = true, subpixel = true)
)

private fun parseLanguage(context: Context, str: String) = when (str) {
    "system" -> Locale(context.resources.configuration.locale.language)
    else -> Locale(str)
}