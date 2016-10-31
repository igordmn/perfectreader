package com.dmi.util.lang

import java.lang.Math.*

infix fun Int.modPositive(n: Int): Int {
    var result = this % n
    if (result < 0) {
        result += n
    }
    return result
}

infix fun Double.modPositive(n: Double): Double {
    var result = this % n
    if (result < 0) {
        result += n
    }
    return result
}

fun clamp(value: Float, min: Float, max: Float) = Math.max(min, Math.min(max, value))
fun clamp(value: Double, min: Double, max: Double) = Math.max(min, Math.min(max, value))
fun clamp(value: Int, min: Int, max: Int) = Math.max(min, Math.min(max, value))

fun floor(value: Float) = floor(value.toDouble()).toFloat()
fun ceil(value: Float) = ceil(value.toDouble()).toFloat()
fun intFloor(value: Float) = floor(value).toInt()
fun intFloor(value: Double) = floor(value).toInt()
fun intRound(value: Double) = round(value).toInt()
fun doubleRound(value: Double) = round(value).toDouble()
fun intCeil(value: Float) = ceil(value).toInt()
fun intCeil(value: Double) = ceil(value).toInt()
fun longFloor(value: Double) = floor(value).toLong()
fun longCeil(value: Double) = ceil(value).toLong()
fun isFractional(value: Double) = value.toInt().toDouble() != value
fun roundThreeFrac(value: Float) = round(value * 1000F) / 1000F

infix fun Float.sameSign(b: Float) = this >= 0 && b >= 0 || this < 0 && b < 0
infix fun Float.notSameSign(b: Float) = !(this sameSign b)