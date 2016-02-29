package com.dmi.util.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import com.google.common.base.Charsets
import com.google.common.io.CharStreams

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

import java.lang.String.format
import java.util.Arrays.sort

object DatabaseUpgrades {
    @Throws(DowngradeException::class)
    fun upgradeDatabase(context: Context, database: SQLiteDatabase, scriptsAssetsPath: String) {
        database.beginTransaction()
        try {
            val newScripts = getNewScripts(context, database.version, scriptsAssetsPath)
            if (newScripts.version < database.version) {
                throw DowngradeException(
                        format("Database cannot be downgraded. Database version: %s, scripts version: %s",
                                database.version,
                                newScripts.version))
            }
            applyScripts(context, database, newScripts.paths)
            database.version = newScripts.version
            database.setTransactionSuccessful()
        } catch (e: IOException) {
            throw RuntimeException("Upgrade database error", e)
        } catch (e: SQLException) {
            throw RuntimeException("Upgrade database error", e)
        } finally {
            database.endTransaction()
        }
    }

    @Throws(IOException::class)
    private fun getNewScripts(context: Context, currentVersion: Int, scriptsAssetsPath: String): NewScripts {
        val newScripts = NewScripts()
        val scripts = context.assets.list(scriptsAssetsPath)
        sort(scripts)
        for (script in scripts) {
            if (script.endsWith(".sql")) {
                val scriptName = script.substring(0, script.indexOf('.'))
                val scriptVersion = Integer.parseInt(scriptName)
                if (scriptVersion > currentVersion) {
                    newScripts.paths.add(scriptsAssetsPath + '/' + script)
                }
                newScripts.version = scriptVersion
            }
        }
        return newScripts
    }

    @Throws(IOException::class)
    private fun applyScripts(context: Context, database: SQLiteDatabase, scriptPaths: List<String>) {
        for (scriptPath in scriptPaths) {
            for (sqlCommand in readSQLCommands(context, scriptPath)) {
                database.execSQL(sqlCommand)
            }
        }
    }

    @SuppressLint("NewApi")
    @Throws(IOException::class)
    private fun readSQLCommands(context: Context, scriptPath: String): List<String> {
        context.assets.open(scriptPath).use { `is` -> return CharStreams.readLines(InputStreamReader(`is`, Charsets.UTF_8)) }
    }

    private class NewScripts {
        val paths: MutableList<String> = ArrayList()
        var version: Int = 0
    }

    class DowngradeException(detailMessage: String) : Exception(detailMessage)
}
