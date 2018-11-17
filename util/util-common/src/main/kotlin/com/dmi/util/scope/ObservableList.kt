package com.dmi.util.scope

import com.dmi.util.collection.removeLast

class ObservableList<T : Any>: Iterable<T> {
    private val list = ArrayList<T>()

    val afterAdd = EmittableEvent()
    val afterRemove = EmittableEvent()

    val top: T? get() = list.lastOrNull()
    val size: Int get() = list.size

    fun add(item: T) {
        list.add(item)
        afterAdd.emit()
    }

    fun remove(): T? {
        val item = list.removeLast()
        afterRemove.emit()
        return item
    }

    operator fun get(index: Int) = list[index]
    override fun iterator(): Iterator<T> = list.iterator()
}

fun <T : Any> ObservableList<T>.bind(list: ArrayList<T>): ObservableList<T> {
    for (item in list) {
        add(item)
    }
    afterAdd.subscribe {
        list.add(top!!)
    }
    afterRemove.subscribe {
        list.removeLast()
    }
    return this
}