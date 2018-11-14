package com.dmi.perfectreader.ui.settings.place.screen

import com.dmi.perfectreader.R
import com.dmi.perfectreader.settings.*
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.*
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.view.Places
import com.dmi.util.android.view.string
import com.dmi.util.system.Nanos
import com.dmi.util.system.minutes
import com.dmi.util.system.toMinutes

fun Places.screen(model: SettingsUI, settings: ScreenSettings, glContext: GLContext) =place {
    fun formatFooterElements(footerElements: ScreenFooterElements): String {
        val count = footerElements.toArray().count { it }
        return if (count == 0) {
            context.string(R.string.settingsUIScreenFooterHide)
        } else {
            context.resources.getQuantityString(R.plurals.settingsUIScreenFooterCount, count, count)
        }
    }

    fun formatTimeout(time: Nanos) = when (time) {
        -1L -> context.string(R.string.settingsUIScreenTimeoutSystem)
        else -> {
            val minutes = time.toMinutes().toInt()
            context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        }
    }

    fun formatBrightness(brightness: ScreenBrightness): String = when (brightness) {
        is ScreenBrightness.System -> context.string(R.string.settingsUIScreenBrightnessSystem)
        is ScreenBrightness.Manual -> {
            val percentStr = (brightness.value * 100).toInt().toString()
            "$percentStr %"
        }
    }

    fun formatOrientation(orientation: ScreenOrientation) = context.string(when (orientation) {
        ScreenOrientation.SYSTEM -> R.string.settingsUIScreenOrientationSystem
        ScreenOrientation.PORTRAIT -> R.string.settingsUIScreenOrientationPortrait
        ScreenOrientation.LANDSCAPE -> R.string.settingsUIScreenOrientationLandscape
    })

    val animation = place {
        screenAnimationDetails(model, model.reader.book, glContext)
    }

    val timeout = singleChoice(
            model, settings::timeout,
            arrayOf(-1L, minutes(1.0), minutes(2.0), minutes(5.0), minutes(10.0), minutes(30.0)),
            ::formatTimeout, R.string.settingsUIScreenTimeout
    )

    val brightness = screenBrightness(context, model)

    val orientation = singleChoice(
            model, settings::orientation,
            ScreenOrientation.values(),
            ::formatOrientation, R.string.settingsUIScreenOrientation
    )

    val footerElements = multiChoice(
            model, settings::footerElementsArray,
            arrayOf(R.string.settingsUIScreenFooterPageNumber, R.string.settingsUIScreenFooterNumberOfPages, R.string.settingsUIScreenFooterChapter),
            R.string.settingsUIScreenFooter
    )

    verticalScroll(
            detailsSetting(model, screenAnimationPreview(glContext), animation, R.string.settingsUIScreenAnimation),
            popupSetting(
                    model,
                    propertyPreview(settings::footerElements, ::formatFooterElements),
                    footerElements,
                    R.string.settingsUIScreenFooter
            ),
            popupSetting(
                    model,
                    propertyPreview(settings::timeout, ::formatTimeout),
                    timeout,
                    R.string.settingsUIScreenTimeout
            ),
            popupSetting(
                    model,
                    propertyPreview(settings::brightness, ::formatBrightness),
                    brightness,
                    R.string.settingsUIScreenBrightness
            ),
            popupSetting(
                    model,
                    propertyPreview(settings::orientation, ::formatOrientation),
                    orientation,
                    R.string.settingsUIScreenOrientation
            )
    )
}