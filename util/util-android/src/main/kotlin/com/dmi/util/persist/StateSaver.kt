package com.dmi.util.persist

import android.os.Bundle
import java.io.Serializable
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StateSaver(private val bundleKey: String) {
    private val holders = ArrayList<ValueHolder<*>>()

    fun <V : Serializable?> register(initial: V): ReadWriteProperty<Any, V> =
            ValueHolder(initial, valueKey()).apply { holders.add(this) }

    private fun valueKey() = "VALUE" + holders.size

    fun restore(bundle: Bundle) {
        val valuesBundle = bundle.getBundle(bundleKey)
        if (valuesBundle != null) {
            holders.forEach { it.restore(valuesBundle) }
        }
    }
    fun save(bundle: Bundle) {
        val valuesBundle = Bundle.EMPTY
        holders.forEach { it.save(valuesBundle) }
        bundle.putBundle(bundleKey, valuesBundle)
    }

    protected class ValueHolder<V : Serializable?>(initial: V, val key: String) : ReadWriteProperty<Any, V> {
        private @Volatile var value = initial

        override fun getValue(thisRef: Any, property: KProperty<*>) = value

        override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
            this.value = value
        }

        @Suppress("UNCHECKED_CAST")
        fun restore(bundle: Bundle) {
            value = bundle.getSerializable(key) as V
        }

        fun save(bundle: Bundle) {
            bundle.putSerializable(key, value)
        }
    }
}