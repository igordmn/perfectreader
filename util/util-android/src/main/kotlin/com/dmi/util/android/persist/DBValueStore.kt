package com.dmi.util.android.persist

import android.database.sqlite.SQLiteDatabase
import com.dmi.util.android.db.getNullOrDouble
import com.dmi.util.android.db.getNullOrLong
import com.dmi.util.android.db.getNullOrString
import com.dmi.util.lang.unsupported
import com.dmi.util.persist.ValueStore
import com.google.common.io.BaseEncoding.base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.compiledSerializer
import kotlinx.serialization.context.getOrDefault
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import kotlin.reflect.KClass

class DBValueStore(
        private val database: SQLiteDatabase,
        private val schema: Schema
) : ValueStore {
    private val keyToValue = HashMap<String, DBValue<*>>()

    override fun <T : Any> value(key: String, default: T, cls: KClass<T>): ValueStore.Value<T> = DBValue(key, default, cls)

    suspend fun load() {
        withContext(Dispatchers.IO) {
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
            private var value: T,
            private val cls: KClass<T>
    ) : ValueStore.Value<T> {
        init {
            keyToValue[key] = this
        }

        fun load(intValue: Long?, realValue: Double?, textValue: String?) {
            @Suppress("UNCHECKED_CAST")
            val newValue = when {
                value is Short -> intValue?.toShort() as T?
                value is Int -> intValue?.toInt() as T?
                value is Long -> intValue as T?
                value is Float -> realValue?.toFloat() as T?
                value is Double -> realValue as T?
                value is Boolean -> intValue?.let { it == 1L } as T?
                value is String -> textValue as T?
                value is Enum<*> -> textValue?.let { enumValueOrNull(cls.java as Class<Enum<*>>, it) } as T?
                CBOR.isSupported(cls) -> textValue?.let { CBOR.load(cls, base64().decode(it)) }
                else -> unsupported(value)
            }
            if (newValue != null)
                value = newValue
        }

        private fun enumValueOrNull(
                enumClass: Class<Enum<*>>,
                text: String
        ) = enumClass.enumConstants.firstOrNull { it.name == text }

        override fun get(): T = value

        override fun set(value: T) {
            this.value = value
            GlobalScope.launch(Dispatchers.IO) {
                when {
                    value is Short -> saveInt(key, value.toLong())
                    value is Int -> saveInt(key, value.toLong())
                    value is Long -> saveInt(key, value)
                    value is Float -> saveReal(key, value.toDouble())
                    value is Double -> saveReal(key, value)
                    value is Boolean -> saveInt(key, if (value) 1L else 0L)
                    value is String -> saveText(key, value)
                    value is Enum<*> -> saveText(key, value.toString())
                    CBOR.isSupported(cls) -> saveText(key, base64().encode(CBOR.dump(cls, value)))
                    else -> unsupported(value)
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

private fun <T : Any> CBOR.Companion.isSupported(cls: KClass<T>): Boolean {
    val serializer = CBOR.plain.context[cls] ?: cls.compiledSerializer()
    return serializer != null
}

private fun <T : Any> CBOR.Companion.dump(cls: KClass<T>, obj: T) = CBOR.plain.dump(CBOR.plain.context.getOrDefault(cls), obj)
private fun <T : Any> CBOR.Companion.load(cls: KClass<T>, raw: ByteArray) = CBOR.plain.load(CBOR.plain.context.getOrDefault(cls), raw)