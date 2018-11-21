package com.dmi.perfectreader.ui.settings.place.control

import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.android.view.Places

// todo triangle zones

fun Places.control(model: SettingsUI, settings: ControlSettings) = place {
    val tapConfigurations = listOf(
            TouchZoneConfiguration.SINGLE,
            TouchZoneConfiguration.FOUR,
            TouchZoneConfiguration.NINE,
            TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
            TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
            TouchZoneConfiguration.SIXTEEN_FIXED
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
            control(
                    controlToolbar(R.string.settingsUIControlSingleTaps, it),
                    controlTaps(
                            tapConfigurations, settings.touches.singleTaps::configuration, settings.touches.singleTaps::property
                    )
            )
        }

        val longTaps = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlLongTaps, it),
                    controlTaps(
                            tapConfigurations, settings.touches.longTaps::configuration, settings.touches.longTaps::property
                    )
            )
        }

        val doubleTaps = controlDialog(model) {
            control(
                    controlDoubleTapsToolbar(R.string.settingsUIControlDoubleTaps, it, settings.touches::doubleTapEnabled),
                    controlDoubleTaps(
                            controlTaps(tapConfigurations, settings.touches.doubleTaps::configuration, settings.touches.doubleTaps::property),
                            settings.touches::doubleTapEnabled
                    )
            )
        }

        val horizontalScrolls = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlHorizontalScrolls, it),
                    controlDirectionTaps(
                            horizontalScrollConfigurations,
                            settings.touches.leftScrolls::configuration, settings.touches.rightScrolls::configuration,
                            settings.touches.leftScrolls::horizontalProperty, settings.touches.rightScrolls::horizontalProperty,
                            R.drawable.ic_long_arrow_left, R.drawable.ic_long_arrow_right,
                            LinearLayoutCompat.HORIZONTAL
                    )
            )
        }

        val verticalScrolls = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlVerticalScrolls, it),
                    controlDirectionTaps(
                            verticalScrollConfigurations,
                            settings.touches.upScrolls::configuration, settings.touches.downScrolls::configuration,
                            settings.touches.upScrolls::verticalProperty, settings.touches.downScrolls::verticalProperty,
                            R.drawable.ic_long_arrow_up, R.drawable.ic_long_arrow_down,
                            LinearLayoutCompat.VERTICAL
                    )
            )
        }

        view {
            details(
                    model, R.string.settingsUIControlOneFinger,
                    verticalScroll(
                            popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                            popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                            popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
                            popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
                            popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                    )
            )
        }
    }

    val twoFingers = place {
        val singleTaps = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlSingleTaps, it),
                    controlTaps(
                            tapConfigurations,
                            settings.touches.twoFingersSingleTaps::configuration, settings.touches.twoFingersSingleTaps::property
                    )
            )
        }

        val longTaps = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlLongTaps, it),
                    controlTaps(
                            tapConfigurations, settings.touches.twoFingersLongTaps::configuration, settings.touches.twoFingersLongTaps::property
                    )
            )
        }

        val doubleTaps = controlDialog(model) {
            control(
                    controlDoubleTapsToolbar(R.string.settingsUIControlDoubleTaps, it, settings.touches::doubleTapEnabled),
                    controlDoubleTaps(
                            controlTaps(tapConfigurations, settings.touches.twoFingersDoubleTaps::configuration, settings.touches.twoFingersDoubleTaps::property),
                            settings.touches::doubleTapEnabled
                    )
            )
        }

        val horizontalScrolls = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlHorizontalScrolls, it),
                    controlDirectionTaps(
                            horizontalScrollConfigurations,
                            settings.touches.twoFingersLeftScrolls::configuration, settings.touches.twoFingersRightScrolls::configuration,
                            settings.touches.twoFingersLeftScrolls::horizontalProperty, settings.touches.twoFingersRightScrolls::horizontalProperty,
                            R.drawable.ic_long_arrow_left, R.drawable.ic_long_arrow_right,
                            LinearLayoutCompat.HORIZONTAL
                    )
            )
        }

        val verticalScrolls = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlVerticalScrolls, it),
                    controlDirectionTaps(
                            verticalScrollConfigurations,
                            settings.touches.twoFingersUpScrolls::configuration, settings.touches.twoFingersDownScrolls::configuration,
                            settings.touches.twoFingersUpScrolls::verticalProperty, settings.touches.twoFingersDownScrolls::verticalProperty,
                            R.drawable.ic_long_arrow_up, R.drawable.ic_long_arrow_down,
                            LinearLayoutCompat.VERTICAL
                    )
            )
        }

        val pinch = controlDialog(model) {
            control(
                    controlToolbar(R.string.settingsUIControlPinch, it),
                    controlPinchTaps(
                            settings.touches.twoFingersPinches::pinchIn, settings.touches.twoFingersPinches::pinchOut,
                            R.drawable.ic_arrows_in, R.drawable.ic_arrows_out,
                            LinearLayoutCompat.VERTICAL
                    )
            )
        }

        view {
            details(
                    model, R.string.settingsUIControlTwoFingers,
                    verticalScroll(
                            popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
                            popupSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
                            popupSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
                            popupSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
                            popupSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls),
                            popupSetting(model, emptyPreview(), pinch, R.string.settingsUIControlPinch)
                    )
            )
        }
    }

    view {
        verticalScroll(
                detailsSetting(model, emptyPreview(), oneFinger, R.string.settingsUIControlOneFinger),
                detailsSetting(model, emptyPreview(), twoFingers, R.string.settingsUIControlTwoFingers)
        )
    }
}