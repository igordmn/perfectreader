package com.dmi.util.graphic

import java.lang.Math.max
import java.lang.Math.min

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
}

infix fun Rect?.union(other: Rect?) = when {
    this != null && other != null -> Rect(min(left, other.left), min(top, other.top), max(right, other.right), max(bottom, other.bottom))
    this != null -> this
    else -> other
}

data class RectF(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    operator fun plus(position: PositionF) = RectF(left + position.x, top + position.y, right + position.x, bottom + position.y)
    operator fun minus(position: PositionF) = RectF(left - position.x, top - position.y, right - position.x, bottom - position.y)
}