package com.dmi.perfectreader.ui.settings.place.control

import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.android.view.Places


// todo triangle zones

//TouchZoneConfiguration.SINGLE,
//TouchZoneConfiguration.FOUR,
//TouchZoneConfiguration.NINE,
//TouchZoneConfiguration.SIXTEEN_FIXED,
//TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
//TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
//TouchZoneConfiguration.TWO_ROWS,
//TouchZoneConfiguration.THREE_ROWS,
//TouchZoneConfiguration.THREE_ROWS_FIXED,
//TouchZoneConfiguration.FOUR_ROWS_FIXED,
//TouchZoneConfiguration.TWO_COLUMNS,
//TouchZoneConfiguration.THREE_COLUMNS,
//TouchZoneConfiguration.THREE_COLUMNS_FIXED,
//TouchZoneConfiguration.FOUR_COLUMNS_FIXED,
//TouchZoneConfiguration.TRIANGLE_SIDES,
//TouchZoneConfiguration.TRIANGLE_SIDES_CENTER

fun Places.control(model: SettingsUI, settings: ControlSettings) = place {
    val tapConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR,
            TouchZoneConfiguration.NINE,
            TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
            TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
            TouchZoneConfiguration.SIXTEEN_FIXED
    )

    val pinchConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR
    )

    val horizontalScrollConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.TWO_ROWS,
            TouchZoneConfiguration.THREE_ROWS,
            TouchZoneConfiguration.THREE_ROWS_FIXED,
            TouchZoneConfiguration.FOUR_ROWS_FIXED
    )

    val verticalScrollConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.TWO_COLUMNS,
            TouchZoneConfiguration.THREE_COLUMNS,
            TouchZoneConfiguration.THREE_COLUMNS_FIXED,
            TouchZoneConfiguration.FOUR_COLUMNS_FIXED
    )

    val oneFinger = place {
        val singleTaps = controlDialog(model) {
            controlTaps(
                    R.string.settingsUIControlSingleTaps, it, tapConfigurations,
                    settings.touches.singleTaps::configuration, settings.touches.singleTaps::property
            )
        }

        val longTaps = controlDialog(model) {
            controlTaps(
                    R.string.settingsUIControlLongTaps, it, tapConfigurations,
                    settings.touches.longTaps::configuration, settings.touches.longTaps::property
            )
        }

        val doubleTaps = controlDialog(model) {
            controlDoubleTaps(
                    R.string.settingsUIControlDoubleTaps, it, tapConfigurations,
                    settings.touches.doubleTaps::configuration, settings.touches.doubleTaps::property, settings.touches::doubleTapEnabled
            )
        }

        val horizontalScrolls = controlDialog(model) {
            controlDirectionTaps(
                    R.string.settingsUIControlHorizontalScrolls, it, horizontalScrollConfigurations,
                    settings.touches.leftScrolls::configuration, settings.touches.rightScrolls::configuration,
                    settings.touches.leftScrolls::horizontalProperty, settings.touches.rightScrolls::horizontalProperty,
                    R.drawable.ic_long_arrow_left, R.drawable.ic_long_arrow_right,
                    LinearLayoutCompat.HORIZONTAL
            )
        }

//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlOneFinger,
                verticalScroll(
                        popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                        popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                        popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
                        popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls)
//                        popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    val twoFingers = place {
        //        val pinch = controlDialog(model)
        val singleTaps = controlDialog(model) {
            controlTaps(
                    R.string.settingsUIControlSingleTaps, it, tapConfigurations,
                    settings.touches.twoFingersSingleTaps::configuration, settings.touches.twoFingersSingleTaps::property
            )
        }

        val longTaps = controlDialog(model) {
            controlTaps(
                    R.string.settingsUIControlLongTaps, it, tapConfigurations,
                    settings.touches.twoFingersLongTaps::configuration, settings.touches.twoFingersLongTaps::property
            )
        }

        val doubleTaps = controlDialog(model) {
            controlDoubleTaps(
                    R.string.settingsUIControlDoubleTaps, it, tapConfigurations,
                    settings.touches.twoFingersDoubleTaps::configuration, settings.touches.twoFingersDoubleTaps::property, settings.touches::doubleTapEnabled
            )
        }
//        val horizontalScrolls = controlDialog(model)
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlTwoFingers,
                verticalScroll(
//                        popupSetting(model, emptyPreview(), pinch, R.string.settingsUIControlPinch),
                        popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                        popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                        popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps)
//                        popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    verticalScroll(
            detailsSetting(model, emptyPreview(), oneFinger, R.string.settingsUIControlOneFinger),
            detailsSetting(model, emptyPreview(), twoFingers, R.string.settingsUIControlTwoFingers)
    )
}