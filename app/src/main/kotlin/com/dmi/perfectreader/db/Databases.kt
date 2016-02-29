package com.dmi.perfectreader.db

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import com.dmi.util.db.DatabaseUpgrades
import com.dmi.util.db.DatabaseUpgrades.upgradeDatabase
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class Databases {
    private lateinit var user: SQLiteDatabase

    @Named("applicationContext")
    @Inject
    lateinit  var context: Context

    protected fun finalize() {
        // it's safe keep databases opened during application life. @see http://stackoverflow.com/questions/6608498/best-place-to-close-database-connection
        close()
    }

    @Synchronized fun init() {
        user = context.openOrCreateDatabase("user", MODE_PRIVATE, null)
        try {
            upgradeDatabase(context, user, "db/user")
        } catch (e: DatabaseUpgrades.DowngradeException) {
            throw RuntimeException(e) // todo показывать диалог
        }

    }

    private fun close() {
        user.close()
    }

    @Synchronized fun user(): SQLiteDatabase {
        return user
    }
}
