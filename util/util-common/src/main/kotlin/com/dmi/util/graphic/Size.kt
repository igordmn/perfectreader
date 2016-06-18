package com.dmi.util.graphic

data class Size(val width: Int, val height: Int) {
    fun toFloat() = SizeF(width.toFloat(), height.toFloat())
}

data class SizeF(val width: Float, val height: Float) {
    operator fun times(multiplier: Float) = SizeF(width * multiplier, height * multiplier)
    operator fun div(divider: Float) = SizeF(width / divider, height / divider)
}