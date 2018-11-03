package com.dmi.util.collection

import java.util.*

inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>) =
        EnumMap<K, V>(K::class.java).apply { putAll(pairs) }


inline fun <T> Array<out T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0.0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0.0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}