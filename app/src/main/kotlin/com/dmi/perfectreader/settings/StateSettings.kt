package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class StateSettings(store: ValueStore) {
    var isLibrary: Boolean by store.value(true)
    var bookUri: String by store.value("")
}