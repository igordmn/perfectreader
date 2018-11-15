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
    val font by store.group(::FontSettings)
    val format by store.group(::FormatSettings)
    val styles: Styles by store.group(::Styles)
    val theme by store.group(::ThemeSettings)
    val control by store.group(::ControlSettings)
    val screen by store.group(::ScreenSettings)
    val other by store.group(::OtherSettings)
}

fun Settings.switchStyle() = styles.applyNext(theme)