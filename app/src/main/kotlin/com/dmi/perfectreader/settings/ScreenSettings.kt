package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class ScreenSettings(store: ValueStore) {
    var timeout by store.value(-1L)
}