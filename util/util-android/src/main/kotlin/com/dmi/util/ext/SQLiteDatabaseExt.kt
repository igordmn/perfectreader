package com.dmi.util.ext

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.regex.Pattern

inline fun <T> SQLiteDatabase.execQuery(sql: String, vararg args: Pair<String, Any>, accept: Cursor.() -> T): T {
    rawQuery(applyArguments(sql, *args), null).use {
        return accept(it)
    }
}

fun Cursor.getNullOrString(columnIndex: Int) =
        if (isNull(columnIndex)) null else getString(columnIndex)

fun Cursor.getNullOrDouble(columnIndex: Int) =
        if (isNull(columnIndex)) null else getDouble(columnIndex)

fun Cursor.getNullOrLong(columnIndex: Int) =
        if (isNull(columnIndex)) null else getLong(columnIndex)

// Все, что ниже, скопировано из org.jetbrains.anko.db.Database

private val ARG_PATTERN: Pattern = Pattern.compile("([^\\\\])\\{([^\\{}]+)\\}")

fun applyArguments(whereClause: String, vararg args: Pair<String, Any>): String {
    val argsMap = args.fold(hashMapOf<String, Any>()) { map, arg ->
        map.put(arg.first, arg.second)
        map
    }
    return applyArguments(whereClause, argsMap)
}

fun applyArguments(whereClause: String, args: Map<String, Any>): String {
    val matcher = ARG_PATTERN.matcher(whereClause)
    val buffer = StringBuffer(whereClause.length)
    while (matcher.find()) {
        val key = matcher.group(2)
        val value = args[key] ?: throw IllegalStateException("Can't find a value for key $key")

        val valueString = if (value is Int || value is Long || value is Byte || value is Short) {
            value.toString()
        } else if (value is Boolean) {
            if (value) "1" else "0"
        } else if (value is Float || value is Double) {
            value.toString()
        } else {
            '\'' + value.toString().replace("'", "''") + '\''
        }
        matcher.appendReplacement(buffer, matcher.group(1) + valueString)
    }
    matcher.appendTail(buffer)
    return buffer.toString()
}