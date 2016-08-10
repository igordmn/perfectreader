package com.dmi.util.graphic

data class PositionF(val x: Float, val y: Float) {
    operator fun plus(other: PositionF) = PositionF(x + other.x, y + other.y)
    operator fun minus(other: PositionF) = PositionF(x - other.x, y - other.y)
}