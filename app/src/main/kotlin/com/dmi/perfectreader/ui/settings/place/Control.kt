package com.dmi.perfectreader.ui.settings.place

import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.verticalScroll
import com.dmi.util.android.view.Places

fun Places.control(model: SettingsUI, settings: ControlSettings) = place {
    verticalScroll()
}