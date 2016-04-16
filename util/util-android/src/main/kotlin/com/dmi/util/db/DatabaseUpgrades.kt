package com.dmi.util.db

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.google.common.base.Charsets
import com.google.common.io.CharStreams.readLines
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Integer.parseInt
import java.util.*
import java.util.Arrays.sort

fun upgradeDatabase(context: Context, db: SQLiteDatabase, scriptsAssetsPath: String) {
    db.beginTransaction()
    try {
        val newScripts = findNewScripts(context, db.version, scriptsAssetsPath)
        if (newScripts.version < db.version) {
            throw DatabaseDowngradeException(
                    "Database cannot be downgraded. Database version: ${db.version}, scripts version: ${newScripts.version}"
            )
        }
        applyScripts(context, db, newScripts.paths)
        db.version = newScripts.version
        db.setTransactionSuccessful()
    } catch (e: IOException) {
        throw RuntimeException("Upgrade database error", e)
    } catch (e: SQLException) {
        throw RuntimeException("Upgrade database error", e)
    } finally {
        db.endTransaction()
    }
}

private fun findNewScripts(context: Context, currentVersion: Int, scriptsAssetsPath: String): NewScripts {
    val newScripts = NewScripts()
    val scripts = context.assets.list(scriptsAssetsPath)
    sort(scripts)
    for (script in scripts) {
        if (script.endsWith(".sql")) {
            val name = script.substring(0, script.indexOf('.'))
            val version = parseInt(name)
            if (version > currentVersion)
                newScripts.paths.add("$scriptsAssetsPath/$script")
            newScripts.version = version
        }
    }
    return newScripts
}

private fun applyScripts(context: Context, database: SQLiteDatabase, scriptPaths: List<String>) {
    for (scriptPath in scriptPaths) {
        for (sqlCommand in readSQLCommands(context, scriptPath)) {
            database.execSQL(sqlCommand)
        }
    }
}

private fun readSQLCommands(context: Context, scriptPath: String): List<String> {
    context.assets.open(scriptPath).use { stream ->
        return readLines(InputStreamReader(stream, Charsets.UTF_8))
    }
}

private class NewScripts {
    val paths: MutableList<String> = ArrayList()
    var version: Int = 0
}

class DatabaseDowngradeException(detailMessage: String) : Exception(detailMessage)