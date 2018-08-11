package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ImageSettings(store: ValueStore) {
    var sourceScaleByDpi by store.value(true)
    var sourceScale by store.value(1F)
    var scaleFiltered by store.value(true)
}