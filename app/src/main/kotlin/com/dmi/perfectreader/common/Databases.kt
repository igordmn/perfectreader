package com.dmi.perfectreader.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.dmi.util.android.db.DatabaseDowngradeException
import com.dmi.util.android.db.upgradeDatabase
import com.dmi.util.log.Log

class Databases(
        private val log: Log,
        private val context: Context
) {
    val default = context.openOrCreateDatabase("default", MODE_PRIVATE, null)

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
            upgradeDatabase(context, default, "db")
        } catch (e: DatabaseDowngradeException) {
            throw RuntimeException(e)
        }
    }

    private fun close() = default.close()
}