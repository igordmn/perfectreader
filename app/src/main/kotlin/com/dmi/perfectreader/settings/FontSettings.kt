package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class FontSettings(store: ValueStore) {
    var fontFamily by store.value("")
    var isBold by store.value(false)
    var isItalic by store.value(false)
    var sizeDip by store.value(20F)
    var scaleX by store.value(1.0F)
    var strokeWidthEm by store.value(0.0F)
    var skewX by store.value(0.0F)
    var antialiasing by store.value(true)
    var hinting by store.value(true)
    var subpixelPositioning by store.value(true)
}