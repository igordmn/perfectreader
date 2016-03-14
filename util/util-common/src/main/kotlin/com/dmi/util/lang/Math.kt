package com.dmi.util.lang

fun modPositive(x: Int, n: Int): Int {
    var result = x % n
    if (result < 0) {
        result += n
    }
    return result
}

fun clamp(min: Double, max: Double, value: Double): Double {
    return Math.max(min, Math.min(max, value))
}

fun clamp(min: Float, max: Float, value: Float): Float {
    return Math.max(min, Math.min(max, value))
}
