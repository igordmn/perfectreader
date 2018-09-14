package com.dmi.util.lang

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ReadOnlyProperty2<in R, out T>: ReadOnlyProperty<R, T> {
    val value : T
    override operator fun getValue(thisRef: R, property: KProperty<*>): T = value
}

interface ReadWriteProperty2<in R, T>: ReadWriteProperty<R, T> {
    var value : T
    override operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> value(initial: T) = object : ReadWriteProperty2<Any?, T> {
    override var value = initial
}

fun <T> ReadWriteProperty2<Any?, T>.set(afterSet: (value: T) -> Unit) = object : ReadWriteProperty2<Any?, T> {
    override var value: T
        get() = this@set.value
        set(value) {
            this@set.value = value
            afterSet(value)
        }
}

fun <T : Any> initOnce() = object : ReadWriteProperty<Any?, T> {
    private var initialized = false
    private lateinit var value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        require(initialized) { "Property ${property.name} should be initialized" }
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        require(!initialized) { "Property ${property.name} is already initialized" }
        this.value = value
        initialized = true
    }
}

fun <T> threadLocal() = object : ReadWriteProperty<Any?, T> {
    private val value = ThreadLocal<T>()
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value.get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this.value.set(value)
}

fun <T> threadLocal(initial: T) = object : ReadWriteProperty<Any?, T> {
    private val value = ThreadLocal<T>()
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value.get() ?: initial
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this.value.set(value)
}