package com.dmi.perfectreader.ui.settings.place

import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.FormatSettings
import com.dmi.perfectreader.settings.pagePadding
import com.dmi.perfectreader.settings.textJustify
import com.dmi.perfectreader.ui.settings.SettingValues
import com.dmi.perfectreader.ui.settings.common.booleanSetting
import com.dmi.perfectreader.ui.settings.common.floatSetting
import com.dmi.perfectreader.ui.settings.common.verticalScroll
import com.dmi.util.android.view.Places

fun Places.format(settings: FormatSettings) = place {
    verticalScroll(
            floatSetting(settings::pagePadding, SettingValues.PARAGRAPH_PADDING, R.string.settingsUIFormatPadding),
            floatSetting(settings::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER, R.string.settingsUIFormatLineHeight),
            floatSetting(settings::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING, R.string.settingsUIFormatLetterSpacing),
            floatSetting(settings::paragraphVerticalMarginEm, SettingValues.PARAGRAPH_VERTICAL_MARGIN, R.string.settingsUIFormatParagraphSpacing),
            floatSetting(settings::paragraphFirstLineIndentEm, SettingValues.FIRST_LINE_INDENT, R.string.settingsUIFormatFirstLineIndent),
            booleanSetting(settings::hyphenation, R.string.settingsUIFormatHyphenation),
            booleanSetting(settings::hangingPunctuation, R.string.settingsUIFormatHangingPunctuation, R.string.settingsUIFormatHangingPunctuationDesc),
            booleanSetting(settings::textJustify, R.string.settingsUIFormatJustify)
    )
}