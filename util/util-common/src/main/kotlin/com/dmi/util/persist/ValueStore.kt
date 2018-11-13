package com.dmi.util.persist

import com.dmi.util.lang.ReadOnlyDelegate
import com.dmi.util.lang.ReadOnlyProperty2
import com.dmi.util.lang.ReadWriteDelegate
import com.dmi.util.lang.ReadWriteProperty2
import com.dmi.util.scope.observable
import kotlin.reflect.KClass

interface ValueStore {
    fun <T : Any> value(key: String, default: T, cls: KClass<T>): Value<T>

    interface Value<T : Any> {
        fun get(): T
        fun set(value: T)
    }
}

class MemoryValueStore : ValueStore {
    private val map = HashMap<String, Any>()
    override fun <T : Any> value(key: String, default: T, cls: KClass<T>) = object : ValueStore.Value<T> {
        @Suppress("UNCHECKED_CAST")
        override fun get() = map.getOrElse(key) { default } as T

        override fun set(value: T) {
            map[key] = value
        }
    }
}

fun ValueStore.substore(storeKey: String) = object : ValueStore {
    override fun <T : Any> value(key: String, default: T, cls: KClass<T>) = this@substore.value("$storeKey.$key", default, cls)
}

fun <T : Any> ValueStore.group(create: (subStore: ValueStore) -> T) = ReadOnlyDelegate<Any, T> {
    val value = create(substore(prop.name))
    object : ReadOnlyProperty2<Any, T> {
        override val value: T get() = value
    }
}

inline fun <reified T : Any> ValueStore.value(default: T) = ReadWriteDelegate<Any, T> {
    val storeValue = value(prop.name, default, T::class)
    object : ReadWriteProperty2<Any, T> {
        override var value: T
            get() = storeValue.get()
            set(value) = storeValue.set(value)
    }
}

class ObservableValueStore(private val valueStore: ValueStore) : ValueStore {
    override fun <T : Any> value(key: String, default: T, cls: KClass<T>): ValueStore.Value<T> {
        val original = valueStore.value(key, default, cls)
        var observableValue by observable(Unit)
        return object : ValueStore.Value<T> {
            override fun get(): T {
                @Suppress("UNUSED_EXPRESSION")
                observableValue // just call for intercept observables
                return original.get()
            }

            override fun set(value: T) {
                original.set(value)
                observableValue = Unit
            }
        }
    }
}