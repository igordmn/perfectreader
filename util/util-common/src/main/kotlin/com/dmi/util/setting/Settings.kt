package com.dmi.util.setting

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Settings {
    abstract class Keys(parent: Keys? = null) {
        protected val prefix = if (parent != null) parent.subname(javaClass.simpleName) else ""

        protected fun subname(name: String): String = if (prefix != "") prefix + "." + name else name

        protected fun key(default: Short) = keyProperty { ShortKey(it, default) }
        protected fun key(default: Int) = keyProperty { IntKey(it, default) }
        protected fun key(default: Long) = keyProperty { LongKey(it, default) }
        protected fun key(default: Float) = keyProperty { FloatKey(it, default) }
        protected fun key(default: Double) = keyProperty { DoubleKey(it, default) }
        protected fun key(default: Boolean) = keyProperty { BooleanKey(it, default) }
        protected fun key(default: String) = keyProperty { StringKey(it, default) }
        protected fun <T : Enum<T>> key(default: T) = keyProperty { EnumKey(it, default) }

        protected fun <K> keyProperty(create: (key: String) -> K) = object : ReadOnlyProperty<Keys, K> {
            private var key: K? = null

            override fun getValue(thisRef: Keys, property: KProperty<*>): K {
                if (key == null)
                    key = create(subname(property.name))
                return key!!
            }
        }
    }

    operator fun set(key: ShortKey, value: Short)
    operator fun get(key: ShortKey): Short
    operator fun set(key: IntKey, value: Int)
    operator fun get(key: IntKey): Int
    operator fun set(key: LongKey, value: Long)
    operator fun get(key: LongKey): Long
    operator fun set(key: FloatKey, value: Float)
    operator fun get(key: FloatKey): Float
    operator fun set(key: DoubleKey, value: Double)
    operator fun get(key: DoubleKey): Double
    operator fun set(key: BooleanKey, value: Boolean)
    operator fun get(key: BooleanKey): Boolean
    operator fun set(key: StringKey, value: String)
    operator fun get(key: StringKey): String
    operator fun <T : Enum<T>> set(key: EnumKey<T>, value: T)
    operator fun <T : Enum<T>> get(key: EnumKey<T>): T

    abstract class Key<T>(val name: String, val default: T)
    class ShortKey(name: String, default: Short) : Key<Short>(name, default)
    class IntKey(name: String, default: Int) : Key<Int>(name, default)
    class LongKey(name: String, default: Long) : Key<Long>(name, default)
    class FloatKey(name: String, default: Float) : Key<Float>(name, default)
    class DoubleKey(name: String, default: Double) : Key<Double>(name, default)
    class BooleanKey(name: String, default: Boolean) : Key<Boolean>(name, default)
    class StringKey(name: String, default: String) : Key<String>(name, default)
    class EnumKey<T : Enum<T>>(name: String, default: T) : Key<T>(name, default)
}