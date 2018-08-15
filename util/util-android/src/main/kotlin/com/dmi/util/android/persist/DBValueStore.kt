package com.dmi.util.android.persist

import android.database.sqlite.SQLiteDatabase
import com.dmi.util.android.db.getNullOrDouble
import com.dmi.util.android.db.getNullOrLong
import com.dmi.util.android.db.getNullOrString
import com.dmi.util.coroutine.IOPool
import com.dmi.util.lang.Enums
import com.dmi.util.lang.unsupported
import com.dmi.util.persist.ValueStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select

class DBValueStore(
        private val database: SQLiteDatabase,
        private val schema: Schema
) : ValueStore {
    private val keyToValue = HashMap<String, DBValue<*>>()

    override fun <T : Any> value(key: String, default: T): ValueStore.Value<T> = DBValue(key, default)

    suspend fun load() {
        withContext(IOPool) {
            database.select(
                    schema.table,
                    schema.columns.key,
                    schema.columns.intValue,
                    schema.columns.realValue,
                    schema.columns.textValue
            ).exec {
                while (moveToNext()) {
                    val key = getString(0)
                    keyToValue[key]?.load(
                            intValue = getNullOrLong(1),
                            realValue = getNullOrDouble(2),
                            textValue = getNullOrString(3)
                    )
                }
            }
        }
    }

    class Schema(val table: String, val columns: Columns) {
        class Columns(val key: String, val intValue: String, val realValue: String, val textValue: String)
    }

    private inner class DBValue<T : Any>(
            private val key: String,
            var value: T
    ) : ValueStore.Value<T> {
        init {
            keyToValue[key] = this
        }

        fun load(intValue: Long?, realValue: Double?, textValue: String?) {
            @Suppress("UNCHECKED_CAST")
            val newValue = when (value) {
                is Short -> intValue?.toShort() as T?
                is Int -> intValue?.toInt() as T?
                is Long -> intValue as T?
                is Float -> realValue?.toFloat() as T?
                is Double -> realValue as T?
                is Boolean -> intValue?.let { it == 1L } as T?
                is String -> textValue as T?
                is Enum<*> -> textValue?.let { enumValueOrNull(value.javaClass as Class<Enum<*>>, it) } as T?
                else -> unsupported()
            }
            if (newValue != null)
                value = newValue
        }

        private fun enumValueOrNull(enumClass: Class<Enum<*>>, text: String) = try {
            Enums.unsafeValueOf(enumClass, text)
        } catch (e: Exception) {
            null
        }

        override fun get(): T = value

        override fun set(value: T) {
            this.value = value
            launch(IOPool) {
                when (value) {
                    is Short -> saveInt(key, value.toLong())
                    is Int -> saveInt(key, value.toLong())
                    is Long -> saveInt(key, value)
                    is Float -> saveReal(key, value.toDouble())
                    is Double -> saveReal(key, value)
                    is Boolean -> saveInt(key, if (value) 1L else 0L)
                    is String -> saveText(key, value)
                    is Enum<*> -> saveText(key, value.toString())
                    else -> unsupported()
                }
            }
        }

        private fun saveInt(key: String, value: Long) {
            database.replace(schema.table,
                    "key" to key,
                    "intValue" to value
            )
        }

        private fun saveReal(key: String, value: Double) {
            database.replace(schema.table,
                    "key" to key,
                    "realValue" to value
            )
        }

        private fun saveText(key: String, value: String) {
            database.replace(schema.table,
                    "key" to key,
                    "textValue" to value
            )
        }
    }
}