package com.dmi.util.lang

infix fun Int.modPositive(n: Int): Int {
    var result = this % n
    if (result < 0) {
        result += n
    }
    return result
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return Math.max(min, Math.min(max, value))
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return Math.max(min, Math.min(max, value))
}