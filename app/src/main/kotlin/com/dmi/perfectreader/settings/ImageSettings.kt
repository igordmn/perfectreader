package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ImageSettings(store: ValueStore) {
    var scaleByDpi by store.value(true)
    var scaleByDpiInteger by store.value(true)
    var scaleFixed by store.value(1F)
    var scaleIncFiltered by store.value(false)
    var scaleDecFiltered by store.value(true)
}