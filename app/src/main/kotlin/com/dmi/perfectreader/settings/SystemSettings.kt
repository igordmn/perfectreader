package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class SystemSettings(store: ValueStore) {
    var fontsPath by store.value("externalStorage://Fonts")
}