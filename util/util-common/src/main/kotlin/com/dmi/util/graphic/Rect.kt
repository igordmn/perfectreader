package com.dmi.util.graphic

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    val isEmpty: Boolean = right <= left && bottom <= top
}

data class RectF(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    operator fun plus(position: PositionF) = RectF(left + position.x, top + position.y, right + position.x, bottom + position.y)
    operator fun minus(position: PositionF) = RectF(left - position.x, top - position.y, right - position.x, bottom - position.y)
}