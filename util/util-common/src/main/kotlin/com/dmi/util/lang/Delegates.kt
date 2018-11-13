package com.dmi.util.lang

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface ReadOnlyProperty2<in R, out T> : ReadOnlyProperty<R, T> {
    val value: T
    override operator fun getValue(thisRef: R, property: KProperty<*>): T = value
}

interface ReadWriteProperty2<in R, T> : ReadWriteProperty<R, T> {
    var value: T
    override operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> value(initial: T) = object : ReadWriteProperty2<Any?, T> {
    override var value = initial
}

fun <T> value(property: KMutableProperty0<T>) = object : ReadWriteProperty2<Any?, T> {
    override var value
        get() = property.get()
        set(value) {
            property.set(value)
        }
}

fun <T> value(get: () -> T) = object : ReadOnlyProperty2<Any?, T> {
    override val value get() = get()
}

fun <T> ReadWriteProperty2<Any?, T>.set(afterSet: (value: T) -> Unit) = object : ReadWriteProperty2<Any?, T> {
    override var value: T
        get() = this@set.value
        set(value) {
            this@set.value = value
            afterSet(value)
        }
}

fun <T> initOnce() = object : ReadWriteProperty<Any?, T> {
    private var initialized = false
    private lateinit var holder: ValueHolder<T>

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        require(initialized) { "Property ${property.name} should be initialized" }
        return holder.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        require(!initialized) { "Property ${property.name} is already initialized" }
        this.holder = ValueHolder(value)
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

class ReadOnlyDelegate<R, T>(val provide: DelegateContext<R>.() -> ReadOnlyProperty2<R, T>) {
    operator fun provideDelegate(thisRef: R, prop: KProperty<*>) = DelegateContext(thisRef, prop).provide()
}

class ReadWriteDelegate<R, T>(val provide: DelegateContext<R>.() -> ReadWriteProperty2<R, T>) {
    operator fun provideDelegate(thisRef: R, prop: KProperty<*>) = DelegateContext(thisRef, prop).provide()
}

class DelegateContext<R>(val thisRef: R, val prop: KProperty<*>)

fun <R, T : Any> ReadOnlyDelegate<R, T?>.required() = ReadOnlyDelegate<R, T> {
    val context = this
    val original = this@required
    val delegate = original.provide(context)
    value { delegate.value!! }
}