package com.dmi.util.lang

import kotlin.reflect.KMutableProperty0

interface Property<T> {
    var value: T
}

@JvmName("map1")
fun <A, B> map(property: KMutableProperty0<A>, forward: (A) -> B, backward: (B) -> A): KMutableProperty0<B> {
    val prop = object : Property<B> {
        override var value: B
            get() = forward(property.get())
            set(value) = property.set(backward(value))
    }
    return prop::value
}

@JvmName("map2")
fun <A: Any, B: Any> map(property: KMutableProperty0<A?>, forward: (A) -> B, backward: (B) -> A): KMutableProperty0<B?> {
    val prop = object : Property<B?> {
        override var value: B?
            get() = property.get()?.let(forward)
            set(value) = property.set(value?.let(backward))
    }
    return prop::value
}