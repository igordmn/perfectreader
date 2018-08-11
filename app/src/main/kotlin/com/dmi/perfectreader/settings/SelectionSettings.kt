package com.dmi.perfectreader.settings

import com.dmi.util.graphic.Color
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class SelectionSettings(store: ValueStore) {
    var color by store.value(Color(255, 174, 223, 240).value)
    var selectWords by store.value(false)
}