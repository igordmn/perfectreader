package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ScreenSettings(store: ValueStore) {
    var timeout by store.value(-1L)
    var brightnessIsSystem by store.value(true)
    var brightnessValue by store.value(1.0F)
    var orientation: ScreenOrientation by store.value(ScreenOrientation.SYSTEM)
}

val ScreenSettings.brightness get() = if (brightnessIsSystem) ScreenBrightness.System else ScreenBrightness.Manual(brightnessValue)

sealed class ScreenBrightness {
    object System : ScreenBrightness()
    class Manual(val value: Float): ScreenBrightness()
}

enum class ScreenOrientation {
    SYSTEM,
    PORTRAIT,
    LANDSCAPE
}