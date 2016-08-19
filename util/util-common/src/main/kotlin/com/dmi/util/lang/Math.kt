package com.dmi.util.lang

infix fun Int.modPositive(n: Int): Int {
    var result = this % n
    if (result < 0) {
        result += n
    }
    return result
}

fun clamp(value: Float, min: Float, max: Float) = Math.max(min, Math.min(max, value))
fun clamp(value: Int, min: Int, max: Int) = Math.max(min, Math.min(max, value))

fun floor(value: Float) = Math.floor(value.toDouble()).toFloat()
fun ceil(value: Float) = Math.ceil(value.toDouble()).toFloat()
fun intFloor(value: Float) = floor(value).toInt()
fun intCeil(value: Float) = ceil(value).toInt()

infix fun Float.sameSign(b: Float) = this >= 0 && b >= 0 || this < 0 && b < 0
infix fun Float.notSameSign(b: Float) = !(this sameSign b)