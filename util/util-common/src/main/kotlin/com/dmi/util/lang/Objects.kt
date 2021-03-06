package com.dmi.util.lang

inline fun <reified T : Any> T.safeEquals(other: Any?, equals: T.(other: T) -> Boolean): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false
    return equals(this, other as T)
}

fun hashCode(value: Float) = java.lang.Float.floatToIntBits(value)

fun hashCode(val1: Int, val2: Int): Int {
    var result = val1
    result += 31 * result + val2
    return result
}