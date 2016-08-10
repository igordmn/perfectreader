package com.dmi.util.rx

import rx.subjects.Subject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Any?> rxObservable(initial: T, subject: Subject<T, T>) = object : ReadWriteProperty<Any?, T> {
    private var value = initial

    init {
        subject.onNext(initial)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != this.value) {
            this.value = value
            subject.onNext(value)
        }
    }
}