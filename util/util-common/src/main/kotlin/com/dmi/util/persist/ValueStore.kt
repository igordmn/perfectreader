package com.dmi.util.persist

import com.dmi.util.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ValueStore {
    fun <T : Any> value(key: String, default: T): Value<T>

    interface Value<T : Any> {
        fun get(): T
        fun set(value: T)
    }
}

fun ValueStore.substore(storeKey: String) = object : ValueStore {
    override fun <T : Any> value(key: String, default: T) = this@substore.value("$storeKey.$key", default)
}

fun <T : Any> ValueStore.group(create: (subStore: ValueStore) -> T) = GroupDelegate(this, create)
fun <T : Any> ValueStore.value(default: T) = ValueDelegate(this, default)

class GroupDelegate<T>(private val store: ValueStore, private val create: (subStore: ValueStore) -> T) {
    operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): ReadOnlyProperty<Any, T> {
        val value = create(store.substore(prop.name))
        return object : ReadOnlyProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T = value
        }
    }
}

class ValueDelegate<T : Any>(private val store: ValueStore, private val default: T) {
    operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): ReadWriteProperty<Any, T> {
        val storeValue = store.value(prop.name, default)
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T = storeValue.get()
            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = storeValue.set(value)
        }
    }
}

class ScopedValueStore(private val scope: Scope, private val valueStore: ValueStore): ValueStore {
    override fun <T : Any> value(key: String, default: T): ValueStore.Value<T> {
        val original = valueStore.value(key, default)
        var scopeValue by scope.value(Unit)
        return object : ValueStore.Value<T> {
            override fun get(): T {
                @Suppress("UNUSED_EXPRESSION")
                scopeValue // just call scopedValue for intercept observables
                return original.get()
            }

            override fun set(value: T) {
                original.set(value)
                scopeValue = Unit
            }
        }
    }
}