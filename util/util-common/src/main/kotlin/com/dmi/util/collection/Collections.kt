package com.dmi.util.collection

import java.util.*

inline fun <T> ArrayList<T>.putIntoBegin(predicate: (T) -> Boolean) {
    val index = indexOfFirst(predicate)
    if (index > 0) {
        add(0, removeAt(index))
    }
}

inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>) =
        EnumMap<K, V>(K::class.java).apply { putAll(pairs) }


inline fun <T> Array<out T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0.0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}