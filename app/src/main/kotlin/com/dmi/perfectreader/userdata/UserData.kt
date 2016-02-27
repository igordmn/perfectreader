package com.dmi.perfectreader.userdata

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserData {
    @Inject
    @Named("userDatabase")
    protected lateinit var userDatabase: SQLiteDatabase

    @SuppressLint("NewApi")
    fun loadLastBookFile(): File? {
        userDatabase.rawQuery("SELECT path FROM lastBook where id = 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val path = cursor.getString(0)
                return File(path)
            } else {
                return null
            }
        }
    }

    fun saveLastBookFile(bookFile: File) {
        val values = ContentValues()
        values.put("id", 1)
        values.put("path", bookFile.absolutePath)
        userDatabase.insertWithOnConflict("lastBook", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    @SuppressLint("NewApi")
    fun loadBookLocation(bookFile: File): Double? {
        userDatabase.rawQuery("SELECT percent FROM bookLocation WHERE path = ?", arrayOf(bookFile.absolutePath)).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0)
            } else {
                return null
            }
        }
    }

    fun saveBookLocation(bookFile: File, percent: Double) {
        val values = ContentValues()
        values.put("path", bookFile.absolutePath)
        values.put("percent", percent)
        userDatabase.insertWithOnConflict("bookLocation", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }
}
