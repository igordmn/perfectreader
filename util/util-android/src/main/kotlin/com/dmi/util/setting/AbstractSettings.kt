package com.dmi.util.setting

import android.content.Context
import android.content.SharedPreferences
import com.dmi.util.TypeConverters
import com.dmi.util.TypeConverters.stringToType
import com.dmi.util.TypeConverters.typeToString
import com.dmi.util.log.Log

abstract class AbstractSettings {
    private lateinit  var sharedPreferences: SharedPreferences

    protected fun init(context: Context, name: String) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private fun <T: Any> loadValue(setting: Setting<T>): T {
        val defValue = setting.toString(setting.defaultValue())
        val valueString = sharedPreferences.getString(setting.name(), defValue)
        try {
            return setting.parseString(valueString)
        } catch (e: TypeConverters.ParseException) {
            Log.w(e, "Parse saved value error")
            return setting.defaultValue()
        }

    }

    protected fun <T: Any> saveValue(setting: Setting<T>, value: T) {
        val valueString = setting.toString(value)
        sharedPreferences.edit().putString(setting.name(), valueString).apply()
    }

    protected fun <T: Any> setting(name: String, defaultValue: T): Setting<T> {
        return Setting(name, defaultValue)
    }

    inner class Setting<T: Any> internal constructor(private val name: String, private val defaultValue: T) {
        private val valueClass: Class<*>
        @Volatile private var cachedValue: T? = null
        private var listener: SettingListener<T>? = null

        init {
            valueClass = defaultValue.javaClass
        }

        fun name(): String {
            return name
        }

        fun defaultValue(): T {
            return defaultValue
        }

        internal fun toString(value: T): String {
            return typeToString(value, valueClass)
        }

        internal fun parseString(string: String): T {
            return stringToType(string, valueClass)
        }

        @Synchronized fun get(): T? {
            if (cachedValue == null) {
                cachedValue = loadValue(this)
            }
            return cachedValue
        }

        @Synchronized fun set(value: T) {
            cachedValue = value
            if (listener != null) {
                listener!!.onValueSet(value)
            }
            saveValue(this, value)
        }

        @Synchronized fun setListener(listener: SettingListener<T>?) {
            this.listener = listener
        }

        @Synchronized fun removeListener() {
            this.listener = null
        }
    }
}
