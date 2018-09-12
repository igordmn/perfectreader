package com.dmi.perfectreader.settings

import android.database.sqlite.SQLiteDatabase
import com.dmi.util.android.persist.DBValueStore
import com.dmi.util.persist.ObservableValueStore
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.group

suspend fun settings(userDatabase: SQLiteDatabase): Settings {
    val store = DBValueStore(userDatabase,
            DBValueStore.Schema(
                    "Setting",
                    DBValueStore.Schema.Columns(
                            "key", "intValue", "realValue", "textValue"
                    )
            )
    )
    val settings = Settings(ObservableValueStore(store))
    store.load()
    return settings
}

class Settings(store: ValueStore) {
    val analyze by store.group(::AnalyzeSettings)
    val format by store.group(::FormatSettings)
    val image by store.group(::ImageSettings)
    val control by store.group(::ControlSettings)
    val navigation by store.group(::NavigationSettings)
    val selection by store.group(::SelectionSettings)
    val system by store.group(::SystemSettings)
}