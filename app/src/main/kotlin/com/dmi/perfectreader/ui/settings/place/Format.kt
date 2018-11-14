package com.dmi.perfectreader.ui.settings.place

import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.FormatSettings
import com.dmi.perfectreader.settings.paddingDip
import com.dmi.perfectreader.settings.textJustify
import com.dmi.perfectreader.ui.settings.SettingValues
import com.dmi.perfectreader.ui.settings.common.booleanSetting
import com.dmi.perfectreader.ui.settings.common.floatSetting
import com.dmi.perfectreader.ui.settings.common.verticalScroll
import com.dmi.util.android.view.Places

fun Places.format(settings: FormatSettings) = place {
    verticalScroll(
            floatSetting(settings::paddingDip, SettingValues.FORMAT_PADDING, R.string.settingsUIFormatPadding),
            floatSetting(settings::lineHeightMultiplier, SettingValues.FORMAT_LINE_HEIGHT_MULTIPLIER, R.string.settingsUIFormatLineHeight),
            floatSetting(settings::letterSpacingEm, SettingValues.FONT_LETTER_SPACING_EM, R.string.settingsUIFormatLetterSpacing),
            floatSetting(settings::paragraphVerticalMarginEm, SettingValues.FORMAT_PARAGRAPH_VERTICAL_MARGIN_EM, R.string.settingsUIFormatParagraphSpacing),
            floatSetting(settings::paragraphFirstLineIndentEm, SettingValues.FORMAT_FIRST_LINE_INDENT_EM, R.string.settingsUIFormatFirstLineIndent),
            booleanSetting(settings::hyphenation, R.string.settingsUIFormatHyphenation),
            booleanSetting(settings::hangingPunctuation, R.string.settingsUIFormatHangingPunctuation, R.string.settingsUIFormatHangingPunctuationDesc),
            booleanSetting(settings::textJustify, R.string.settingsUIFormatJustify)
    )
}