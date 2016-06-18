package com.dmi.perfectreader.fragment.book.obj.common

import com.dmi.perfectreader.data.UserSettings
import java.util.*
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class LayoutConfig(
        val locale: Locale,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val fontSizeMultiplier: Float,
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginMultiplier: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,
        val textRenderParams: TextRenderParams
)

fun settingsLayoutConfig(settings: UserSettings) = LayoutConfig(
        locale = Locale("RU", "ru"),
        firstLineIndent = settings[FormatKeys.firstLineIndent],
        textAlign = settings[FormatKeys.textAlign],
        fontSizeMultiplier = settings[FormatKeys.fontSizeMultiplier],
        lineHeightMultiplier = settings[FormatKeys.lineHeightMultiplier],
        paragraphVerticalMarginMultiplier = settings[FormatKeys.paragraphVerticalMarginMultiplier],
        hangingConfig = if (settings[FormatKeys.hangingPunctuation]) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings[FormatKeys.hyphenation],
        textRenderParams = TextRenderParams(antialias = true, hinting = true, linearScaling = true, subpixel = true)
)