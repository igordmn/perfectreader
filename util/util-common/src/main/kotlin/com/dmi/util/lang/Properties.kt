package com.dmi.util.lang

import kotlin.reflect.KMutableProperty0

interface Property<T> {
    var value: T
}

fun <A: Any, B: Any> init(property: KMutableProperty0<A?>, init: (A) -> B, afterSet: (B) -> A): KMutableProperty0<B?> {
    val prop = object : Property<B?> {
        override var value: B? = property.get()?.let(init)
            set(value) {
                field = value
                property.set(value?.let(afterSet))
            }
    }
    return prop::value
}