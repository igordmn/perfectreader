package com.dmi.perfectreader.settings

import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value
import kotlinx.serialization.Serializable

class Styles(store: ValueStore) {
    var saved: SavedThemes by store.value(DefaultStyles)
    var lastAppliedIndex: Int by store.value(0)
}

@Serializable
class SavedThemes(val list: List<SavedTheme>)

fun Styles.applyNext(themeSettings: ThemeSettings) {
    if (saved.list.isNotEmpty()) {
        var nextStyleIndex = lastAppliedIndex + 1
        if (nextStyleIndex >= saved.list.size)
            nextStyleIndex = 0

        lastAppliedIndex = nextStyleIndex

        themeSettings.load(saved.list[nextStyleIndex])
    }
}