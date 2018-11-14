package com.dmi.perfectreader.ui.settings.place.font

import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.FontSettings
import com.dmi.perfectreader.ui.settings.SettingValues
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.android.view.Places

fun Places.font(model: SettingsUI, settings: FontSettings) = place {
    val family = place {
        fontFamilyDetails(model)
    }

    verticalScroll(
            detailsSetting(model, fontFamilyPreview(context), family, R.string.settingsUIFontFamily),
            titleSetting(fontStyleView(
                    settings::isBold, settings::isItalic,
                    R.string.settingsUIFontStyleBold, R.string.settingsUIFontStyleItalic
            ), R.string.settingsUIFontStyle),
            floatSetting(settings::sizeDip, SettingValues.TEXT_SIZE, R.string.settingsUIFontSize),
            floatSetting(settings::scaleX, SettingValues.TEXT_SCALE_X, R.string.settingsUIFontWidth),
            floatSetting(settings::strokeWidthEm, SettingValues.TEXT_STROKE_WIDTH, R.string.settingsUIFontBoldness),
            floatSetting(settings::skewX, SettingValues.TEXT_SKEW_X, R.string.settingsUIFontSkew),
            booleanSetting(settings::antialiasing, R.string.settingsUIFontAntialiasing),
            booleanSetting(settings::hinting, R.string.settingsUIFontHinting, R.string.settingsUIFontHintingDesc)
    )
}