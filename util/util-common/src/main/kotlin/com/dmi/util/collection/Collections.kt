package com.dmi.util.collection

import java.util.*

inline fun <T> ArrayList<T>.putIntoBegin(predicate: (T) -> Boolean) {
    val index = indexOfFirst(predicate)
    if (index > 0) {
        add(0, removeAt(index))
    }
}