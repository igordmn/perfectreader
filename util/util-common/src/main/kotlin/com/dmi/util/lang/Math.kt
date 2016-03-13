package com.dmi.util.lang

fun modPositive(x: Int, n: Int): Int {
    var result = x % n
    if (result < 0) {
        result += n
    }
    return result
}

fun clamp(min: Double, max: Double, value: Double): Double {
    return Math.min(min, Math.max(max, value))
}

fun clamp(min: Float, max: Float, value: Float): Float {
    return Math.min(min, Math.max(max, value))
}
