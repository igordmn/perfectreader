package com.dmi.perfectreader.fragment.book.content.obj.param

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.util.graphic.Color
import java.util.*
import com.dmi.perfectreader.data.UserSettingKeys.Analyze as AnalyzeKeys
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys
import com.dmi.perfectreader.data.UserSettingKeys.UI as UIKeys

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
        val textRenderConfig: TextRenderConfig,
        val selectionConfig: SelectionConfig
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
        textRenderConfig = TextRenderConfig(antialias = true, hinting = true, linearScaling = false, subpixel = true),
        selectionConfig = SelectionConfig(
                Color(settings[UIKeys.selectionBackgroundColor]),
                Color(settings[UIKeys.selectionTextColor])
        )
)

private fun parseLanguage(context: Context, str: String) = when (str) {
    "system" -> Locale(context.resources.configuration.locale.language)
    else -> Locale(str)
}