package com.dmi.perfectreader.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.dmi.util.db.DatabaseDowngradeException
import com.dmi.util.db.upgradeDatabase
import com.dmi.util.log

class AppDatabases(
        private val context: Context
) {
    val user = context.openOrCreateDatabase("user", MODE_PRIVATE, null)

    init {
        upgrade()
    }

    @Suppress("unused", "ProtectedInFinal")
    protected fun finalize() {
        // it's safe keep databases opened during application life. @see http://stackoverflow.com/questions/6608498/best-place-to-close-database-connection
        try {
            close()
        } catch (e: Exception) {
            log.e(e, "Databases close error")
        }
    }

    private fun upgrade() {
        try {
            upgradeDatabase(context, user, "db/user")
        } catch (e: DatabaseDowngradeException) {
            throw RuntimeException(e)
        }
    }

    private fun close() = user.close()
}