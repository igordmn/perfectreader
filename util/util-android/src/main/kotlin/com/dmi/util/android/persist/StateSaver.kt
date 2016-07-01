package com.dmi.util.android.persist

import android.os.Bundle
import java.io.Serializable
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StateSaver(private val bundleKey: String) {
    private val holders = ArrayList<ValueHolder>()

    fun <V : Serializable?> register(initial: V): ReadWriteProperty<Any, V> =
            SerializableHolder(initial, valueKey()).apply { holders.add(this) }

    private fun valueKey() = "VALUE" + holders.size

    fun restore(bundle: Bundle) {
        val valuesBundle = bundle.getBundle(bundleKey)
        if (valuesBundle != null) {
            holders.forEach { it.restore(valuesBundle) }
        }
    }

    fun save(bundle: Bundle) {
        val valuesBundle = Bundle()
        holders.forEach { it.save(valuesBundle) }
        bundle.putBundle(bundleKey, valuesBundle)
    }

    private interface ValueHolder {
        fun restore(state: Bundle)
        fun save(state: Bundle)
    }

    private class SerializableHolder<V : Serializable?>(initial: V, val key: String) : ReadWriteProperty<Any, V>, ValueHolder {
        private @Volatile var value = initial

        override fun getValue(thisRef: Any, property: KProperty<*>) = value

        override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
            this.value = value
        }

        @Suppress("UNCHECKED_CAST")
        override fun restore(state: Bundle) {
            value = state.getSerializable(key) as V
        }

        override fun save(state: Bundle) {
            state.putSerializable(key, value)
        }
    }
}