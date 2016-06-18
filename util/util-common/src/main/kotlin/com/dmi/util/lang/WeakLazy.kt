package com.dmi.util.lang

import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> weakLazy(create: () -> T) = object : ReadOnlyProperty<Any?, T> {
    private var ref = WeakReference<T>(null)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        var obj = ref.get()
        if (obj == null) {
            obj = create()
            ref = WeakReference<T>(obj)
        }
        return obj
    }
}