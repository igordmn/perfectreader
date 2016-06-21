package com.dmi.util.setting

import android.database.sqlite.SQLiteDatabase
import com.dmi.util.ext.async
import com.dmi.util.ext.getNullOrDouble
import com.dmi.util.ext.getNullOrLong
import com.dmi.util.ext.getNullOrString
import com.dmi.util.setting.Settings.*
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import rx.Scheduler
import java.util.*

class DBSettings(
        private val database: SQLiteDatabase,
        private val schema: Schema,
        private val saveScheduler: Scheduler
) : Settings {
    private val values by lazy {
        database.select(
                schema.table,
                schema.columns.key,
                schema.columns.intValue,
                schema.columns.realValue,
                schema.columns.textValue
        ).exec {
            HashMap<String, Values>().apply {
                while (moveToNext()) {
                    put(
                            getString(0),
                            Values(
                                    getNullOrLong(1),
                                    getNullOrDouble(2),
                                    getNullOrString(3)
                            )
                    )
                }
            }
        }
    }

    override fun set(key: ShortKey, value: Short) = setInt(key, value.toLong())
    override fun get(key: ShortKey) = getInt(key)?.toShort() ?: key.default
    override fun set(key: IntKey, value: Int) = setInt(key, value.toLong())
    override fun get(key: IntKey) = getInt(key)?.toInt() ?: key.default
    override fun set(key: LongKey, value: Long) = setInt(key, value)
    override fun get(key: LongKey) = getInt(key) ?: key.default
    override fun set(key: FloatKey, value: Float) = setReal(key, value.toDouble())
    override fun get(key: FloatKey) = getReal(key)?.toFloat() ?: key.default
    override fun set(key: DoubleKey, value: Double) = setReal(key, value)
    override fun get(key: DoubleKey) = getReal(key) ?: key.default
    override fun set(key: BooleanKey, value: Boolean) = setInt(key, value.toLong())
    override fun get(key: BooleanKey) = getInt(key)?.toBoolean() ?: key.default
    override fun set(key: StringKey, value: String) = setText(key, value)
    override fun get(key: StringKey) = getText(key) ?: key.default
    override fun <T : Enum<T>> set(key: EnumKey<T>, value: T) = setText(key, value.name)
    override fun <T : Enum<T>> get(key: EnumKey<T>) = getText(key)?.enumValue(key.default.javaClass) ?: key.default

    private fun <T> setInt(key: Settings.Key<T>, value: Long) {
        values[key.name] = Values(value, null, null)
        async(saveScheduler) {
            database.replace(schema.table,
                    "key" to key.name,
                    "intValue" to value
            )
        }
    }

    private fun <T> setReal(key: Settings.Key<T>, value: Double) {
        values[key.name] = Values(null, value, null)
        async(saveScheduler) {
            database.replace(schema.table,
                    "key" to key.name,
                    "realValue" to value
            )
        }
    }

    private fun <T> setText(key: Settings.Key<T>, value: String) {
        values[key.name] = Values(null, null, value)
        async(saveScheduler) {
            database.replace(schema.table,
                    "key" to key.name,
                    "textValue" to value
            )
        }
    }

    private fun <T> getInt(key: Settings.Key<T>) = values[key.name]?.intValue
    private fun <T> getReal(key: Settings.Key<T>) = values[key.name]?.realValue
    private fun <T> getText(key: Settings.Key<T>) = values[key.name]?.textValue

    private fun Boolean.toLong() = if (this) 1L else 0L
    private fun Long.toBoolean() = if (this == 1L) true else false

    private fun <T : Enum<T>> String.enumValue(enumClass: Class<T>) = try {
        java.lang.Enum.valueOf(enumClass, this)
    } catch (e: Exception) {
        null
    }

    class Schema(val table: String, val columns: Columns)
    class Columns(val key: String, val intValue: String, val realValue: String, val textValue: String)

    private class Values(val intValue: Long?, val realValue: Double?, val textValue: String?)
}