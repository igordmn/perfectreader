package com.dmi.util.collection

import java.util.*

fun <T : Any> ArrayList<T>.removeLast(): T? = if (size > 0) removeAt(size - 1) else null

fun <T> List<T>.removeAt(indices: Set<Int>): List<T> {
    val newList = LinkedList(this)

    var i = 0
    val it = newList.iterator()

    while (it.hasNext()) {
        it.next()
        if (indices.contains(i))
            it.remove()
        i++
    }

    return ArrayList(newList)
}