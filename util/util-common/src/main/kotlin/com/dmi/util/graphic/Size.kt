package com.dmi.util.graphic

import com.dmi.util.lang.intCeil

data class Size(val width: Int, val height: Int) {
    fun toFloat() = SizeF(width.toFloat(), height.toFloat())
    operator fun times(multiplier: Float) = SizeF(width * multiplier, height * multiplier)
    operator fun div(divider: Float) = SizeF(width / divider, height / divider)
}

data class SizeF(val width: Float, val height: Float) {
    fun toInt() = Size(width.toInt(), height.toInt())
    operator fun times(multiplier: Float) = SizeF(width * multiplier, height * multiplier)
    operator fun div(divider: Float) = SizeF(width / divider, height / divider)
}