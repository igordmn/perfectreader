package com.dmi.perfectreader.settings

import com.dmi.util.graphic.Color
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ThemeSettings(store: ValueStore) {
    var textColor by store.value(Color.BLACK.value)
    var textGammaCorrection by store.value(1F)

    var textShadowEnabled by store.value(false)
    var textShadowAngleDegrees by store.value(0F)
    var textShadowOffsetEm by store.value(0F)
    var textShadowSizeEm by store.value(0.1F)
    var textShadowBlurEm by store.value(0.05F)
    var textShadowColor by store.value(Color.GRAY.withAlpha(128).value)

    var backgroundIsImage by store.value(false)
    var backgroundColor by store.value(Color.WHITE.value)
    var backgroundPath by store.value("assets:///resources/backgrounds/0004.png")
    var backgroundContentAwareResize by store.value(true)

    var selectionColor by store.value(Color(255, 174, 223, 240).value)
}

var ThemeSettings.textShadowOpacity: Float
    get() = Color(textShadowColor).alpha / 255F
    set(value) {
        val color = Color(textShadowColor)
        textShadowColor = color.withAlpha((value * 255).toInt()).value
    }