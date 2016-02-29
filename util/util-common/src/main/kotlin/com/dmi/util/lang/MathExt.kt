package com.dmi.util.lang

object MathExt {
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
}
