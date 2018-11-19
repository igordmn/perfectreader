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
    val styles = place {
        stylesDetails(model)
    }

    fun formatPageIsImage(pageIsPicture: Boolean) = when (pageIsPicture) {
        false -> context.string(R.string.settingsUIThemePageSelectColor)
        true -> context.string(R.string.settingsUIThemePageSelectImage)
    }

    val pageIsImage = singleChoice(
            model, settings::pageIsImage, arrayOf(false, true),
            ::formatPageIsImage, R.string.settingsUIThemePage
    )

    val pagePicture = place {
        themePagePictureDetails(model)
    }

    val underColor = colorPlace(model, settings::underColor, R.string.settingsUIThemeUnderColor)
    val pageColor = colorPlace(model, settings::pageColor, R.string.settingsUIThemePageColor)
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
                                settings::textShadowOpacity, SettingValues.THEME_TEXT_SHADOW_OPACITY, R.string.settingsUIThemeTextShadowOpacity
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowAngleDegrees,
                                SettingValues.THEME_TEXT_SHADOW_ANGLE, R.string.settingsUIThemeTextShadowAngle,
                                ringValues = true
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowOffsetEm, SettingValues.THEME_TEXT_SHADOW_OFFSET, R.string.settingsUIThemeTextShadowOffset
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowSizeEm, SettingValues.THEME_TEXT_SHADOW_SIZE, R.string.settingsUIThemeTextShadowSize
                        ) visibleIf { settings.textShadowEnabled },
                        floatSetting(
                                settings::textShadowBlurEm, SettingValues.THEME_TEXT_SHADOW_BLUR, R.string.settingsUIThemeTextShadowBlur
                        ) visibleIf { settings.textShadowEnabled }
                )
        )
    }

    verticalScroll(
            detailsSetting(model, emptyPreview(), styles, R.string.settingsUIThemeSaved),
            popupSetting(
                    model,
                    propertyPreview(settings::pageIsImage, ::formatPageIsImage),
                    pageIsImage,
                    R.string.settingsUIThemePage
            ),
            detailsSetting(
                    model, themePageImagePreview(), pagePicture, R.string.settingsUIThemePageImage
            ) visibleIf { settings.pageIsImage },
            detailsSetting(
                    model, colorPreview(settings::pageColor), pageColor, R.string.settingsUIThemePageColor
            ) visibleIf { !settings.pageIsImage },
            booleanSetting(
                    settings::pageContentAwareResize, R.string.settingsUIThemePageContentAwareResize
            ) visibleIf { settings.pageIsImage },
            detailsSetting(
                    model, colorPreview(settings::underColor), underColor, R.string.settingsUIThemeUnderColor, R.string.settingsUIThemeUnderColorDesc
            ),
            detailsSetting(model, colorPreview(settings::textColor), textColor, R.string.settingsUIThemeText),
            detailsSetting(model, colorPreview(settings::selectionColor), selectionColor, R.string.settingsUIThemeSelection),
            detailsSetting(model, emptyPreview(), textShadow, R.string.settingsUIThemeTextShadow),
            floatSetting(settings::textGammaCorrection, SettingValues.THEME_GAMMA_CORRECTION, R.string.settingsUIThemeTextGammaCorrection)
    )
}