package com.dmi.perfectreader.ui.settings.place.control

import android.app.Dialog
import android.widget.Button
import android.widget.RelativeLayout
import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.ui.action.ActionID
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.action.TouchZone
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.android.view.Places
import com.dmi.util.android.view.child
import com.dmi.util.android.view.dialog
import com.dmi.util.android.view.params
import org.jetbrains.anko.matchParent
import kotlin.reflect.KMutableProperty0

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
    val oneFinger = place {
        val singleTaps = controlDialog(
                model,
                listOf(
                        TouchZoneConfiguration.SINGLE,
                        TouchZoneConfiguration.FOUR,
                        TouchZoneConfiguration.NINE,
                        TouchZoneConfiguration.THREE_ROWS_TWO_COLUMNS,
                        TouchZoneConfiguration.TWO_ROWS_THREE_COLUMNS,
                        TouchZoneConfiguration.SIXTEEN_FIXED,
                        TouchZoneConfiguration.TRIANGLE_SIDES,
                        TouchZoneConfiguration.TRIANGLE_SIDES_CENTER
                ),
                settings.touches.singleTaps::configuration,
                settings.touches.singleTaps::property
        )
//        val longTaps = controlDialog(model)
//        val doubleTaps = controlDialog(model)
//        val horizontalScrolls = controlDialog(model)
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlOneFinger,
                verticalScroll(
                        popupSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps)
//                        detailsSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
//                        detailsSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
//                        detailsSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        detailsSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    val twoFingers = place {
        //        val pinch = controlDialog(model)
//        val singleTaps = controlDialog(model)
//        val longTaps = controlDialog(model)
//        val doubleTaps = controlDialog(model)
//        val horizontalScrolls = controlDialog(model)
//        val verticalScrolls = controlDialog(model)

        details(
                model, R.string.settingsUIControlTwoFingers,
                verticalScroll(
//                        detailsSetting(model, emptyPreview(), pinch, R.string.settingsUIControlPinch),
//                        detailsSetting(model, emptyPreview(), singleTaps, R.string.settingsUIControlSingleTaps),
//                        detailsSetting(model, emptyPreview(), longTaps, R.string.settingsUIControlLongTaps),
//                        detailsSetting(model, emptyPreview(), doubleTaps, R.string.settingsUIControlDoubleTaps),
//                        detailsSetting(model, emptyPreview(), horizontalScrolls, R.string.settingsUIControlHorizontalScrolls),
//                        detailsSetting(model, emptyPreview(), verticalScrolls, R.string.settingsUIControlVerticalScrolls)
                )
        )
    }

    verticalScroll(
            detailsSetting(model, emptyPreview(), oneFinger, R.string.settingsUIControlOneFinger),
            detailsSetting(model, emptyPreview(), twoFingers, R.string.settingsUIControlTwoFingers)
    )
}

private fun Places.controlDialog(
        model: SettingsUI,
        configurations: List<TouchZoneConfiguration>,
        configurationProperty: KMutableProperty0<TouchZoneConfiguration>,
        actionProperty: (zone: TouchZone) -> KMutableProperty0<ActionID>
//        actions: List<ActionID> // todo action categories
) = dialog {
     Dialog(context, R.style.fullScreenDialog).apply {
         setContentView(RelativeLayout(context).apply {
             child(params(matchParent, matchParent), Button(context))
         })
         setOnDismissListener {
             model.popup = null
         }
     }
}