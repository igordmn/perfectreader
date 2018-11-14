package com.dmi.perfectreader.ui.settings.place.theme

import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.ThemeSettings
import com.dmi.perfectreader.settings.textShadowOpacity
import com.dmi.perfectreader.ui.settings.SettingValues
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.android.view.Places
import com.dmi.util.android.view.string

fun Places.theme(model: SettingsUI, settings: ThemeSettings) = place {
    val saved = place {
        themeSavedDetails(model)
    }

    fun formatBackgroundIsImage(backgroundIsPicture: Boolean) = when (backgroundIsPicture) {
        false -> context.string(R.string.settingsUIThemeBackgroundSelectColor)
        true -> context.string(R.string.settingsUIThemeBackgroundSelectPicture)
    }

    val backgroundIsImage = singleChoice(
            model, settings::backgroundIsImage, arrayOf(false, true),
            ::formatBackgroundIsImage, R.string.settingsUIThemeBackground
    )

    val backgroundPicture = place {
        themeBackgroundPictureDetails(model)
    }

    val backgroundColor = colorPlace(model, settings::backgroundColor, R.string.settingsUIThemeBackgroundColor)
    val textColor = colorPlace(model, settings::textColor, R.string.settingsUIThemeText)
    val selectionColor = colorPlace(model, settings::selectionColor, R.string.settingsUIThemeSelection)

    val textShadow = place {
        val color = colorPlace(model, settings::textShadowColor, R.string.settingsUIThemeTextShadowColor)

        details(
                model, R.string.settingsUIThemeTextShadow,
                verticalScroll(
                        booleanSetting(settings::textShadowEnabled, R.string.settingsUIThemeTextShadowEnabled),
                        detailsSetting(
                                model,
                                colorPreview(settings::textShadowColor), color, R.string.settingsUIThemeTextShadowColor
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowOpacity, SettingValues.TEXT_SHADOW_OPACITY, R.string.settingsUIThemeTextShadowOpacity
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowAngleDegrees,
                                SettingValues.TEXT_SHADOW_ANGLE, R.string.settingsUIThemeTextShadowAngle,
                                ringValues = true
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowOffsetEm, SettingValues.TEXT_SHADOW_OFFSET, R.string.settingsUIThemeTextShadowOffset
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowSizeEm, SettingValues.TEXT_SHADOW_SIZE, R.string.settingsUIThemeTextShadowSize
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowBlurEm, SettingValues.TEXT_SHADOW_BLUR, R.string.settingsUIThemeTextShadowBlur
                        ) visibleIf { settings.textShadowEnabled }
                )
        )
    }

    verticalScroll(
            detailsSetting(model, emptyPreview(), saved, R.string.settingsUIThemeSaved),
            popupSetting(
                    model,
                    propertyPreview(settings::backgroundIsImage, ::formatBackgroundIsImage),
                    backgroundIsImage,
                    R.string.settingsUIThemeBackground
            ),
            detailsSetting(
                    model, themeBackgroundPicturePreview(), backgroundPicture, R.string.settingsUIThemeBackgroundPicture
            ) visibleIf { settings.backgroundIsImage },
            detailsSetting(
                    model, colorPreview(settings::backgroundColor), backgroundColor, R.string.settingsUIThemeBackgroundColor
            ) visibleIf { !settings.backgroundIsImage },
            booleanSetting(
                    settings::backgroundContentAwareResize, R.string.settingsUIThemeBackgroundContentAwareResize
            ) visibleIf { settings.backgroundIsImage },
            detailsSetting(model, colorPreview(settings::textColor), textColor, R.string.settingsUIThemeText),
            detailsSetting(model, colorPreview(settings::selectionColor), selectionColor, R.string.settingsUIThemeSelection),
            detailsSetting(model, emptyPreview(), textShadow, R.string.settingsUIThemeTextShadow),
            floatSetting(settings::textGammaCorrection, SettingValues.GAMMA_CORRECTION, R.string.settingsUIThemeTextGammaCorrection)
    )
}