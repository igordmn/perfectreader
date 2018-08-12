package com.dmi.perfectreader.settings

import android.database.sqlite.SQLiteDatabase
import com.dmi.util.android.persist.DBValueStore
import com.dmi.util.persist.ScopedValueStore
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.group
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope

suspend fun settings(userDatabase: SQLiteDatabase): Settings {
    val scope = Scope()
    val store = DBValueStore(userDatabase,
            DBValueStore.Schema(
                    "Setting",
                    DBValueStore.Schema.Columns(
                            "key", "intValue", "realValue", "textValue"
                    )
            )
    )
    val settings = Settings(ScopedValueStore(scope, store), scope)
    store.load()
    return settings
}

class Settings(store: ValueStore, scope: Scope) : Disposable by scope {
    val analyze by store.group(::AnalyzeSettings)
    val format by store.group(::FormatSettings)
    val image by store.group(::ImageSettings)
    val control by store.group(::ControlSettings)
    val navigation by store.group(::NavigationSettings)
    val selection by store.group(::SelectionSettings)
    val system by store.group(::SystemSettings)
}