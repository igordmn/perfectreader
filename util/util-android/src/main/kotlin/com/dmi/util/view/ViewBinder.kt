package com.dmi.util.view

import android.view.View
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBinder {
    private val viewHolders = ArrayList<ViewHolder<*>>()

    fun <V : View> register(init: ()-> View): ReadOnlyProperty<Any, V> =
            ViewHolder<V>(init).apply { viewHolders.add(this) }

    fun bind() = viewHolders.forEach { it.bind() }
    fun unbind() = viewHolders.forEach { it.unbind() }

    protected class ViewHolder<V : View>(private val init: () -> View) : ReadOnlyProperty<Any, V> {
        private @Volatile var value: V? = null

        override fun getValue(thisRef: Any, property: KProperty<*>): V {
            check(value != null) { "View unbinded" }
            return value!!
        }

        @Suppress("UNCHECKED_CAST")
        fun bind() {
            value = init() as V
        }

        fun unbind() {
            value = null
        }
    }
}