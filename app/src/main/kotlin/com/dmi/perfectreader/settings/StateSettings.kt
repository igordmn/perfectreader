package com.dmi.perfectreader.settings

import com.dmi.perfectreader.ui.library.LibraryLocationsState
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value
import kotlinx.serialization.Serializable

class StateSettings(store: ValueStore) {
    var isLibrary: Boolean by store.value(true)
    var bookUri: String by store.value("")
    var locations: OptionalLibraryLocationsState by store.value(OptionalLibraryLocationsState(null))
}

@Serializable
class OptionalLibraryLocationsState(val state: LibraryLocationsState?)