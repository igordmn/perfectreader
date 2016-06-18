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