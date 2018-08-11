package com.dmi.perfectreader.control

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.perfectreader.data.UserSettings
import com.dmi.util.input.GestureDetector
import com.dmi.util.mainScheduler

fun settingsGestureDetector(density: Float, settings: UserSettings, listener: GestureDetector.Listener) =
        GestureDetector(
                mainScheduler,
                listener,
                settings[UserSettingKeys.Control.Touches.doubleTapEnabled],
                settings[UserSettingKeys.Control.Touches.tapMaxOffset] * density,
                settings[UserSettingKeys.Control.Touches.longTapTimeout],
                settings[UserSettingKeys.Control.Touches.doubleTapTimeout]
        )