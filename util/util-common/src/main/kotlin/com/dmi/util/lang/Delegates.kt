package com.dmi.util.lang

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

infix fun <R, T> ReadWriteProperty<R, T>.afterSet(afterSet: (value: T) -> Unit): ReadWriteProperty<R, T> {
    val self = this
    return object : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>) = self.getValue(thisRef, property)

        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            self.setValue(thisRef, property, value)
            afterSet(value)
        }
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