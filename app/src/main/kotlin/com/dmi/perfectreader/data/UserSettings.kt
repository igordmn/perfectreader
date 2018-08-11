package com.dmi.perfectreader.data

import android.database.sqlite.SQLiteDatabase
import com.dmi.perfectreader.dataAccessScheduler
import com.dmi.util.android.setting.DBSettings
import com.dmi.util.android.setting.DBSettings.Columns
import com.dmi.util.android.setting.DBSettings.Schema
import com.dmi.util.setting.Settings

class UserSettings(userDatabase: SQLiteDatabase) : Settings by DBSettings(
        userDatabase,
        Schema("Setting", Columns("key", "intValue", "realValue", "textValue")),
        dataAccessScheduler
)