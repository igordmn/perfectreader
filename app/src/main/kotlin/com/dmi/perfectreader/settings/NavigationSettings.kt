package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class NavigationSettings(store: ValueStore) {
    var pageSymbolCount by store.value(1024)
    var pageSymbolCountIsAuto by store.value(true)
}