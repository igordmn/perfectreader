package com.dmi.util.graphic

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}

data class PositionF(val x: Float, val y: Float) {
    operator fun unaryMinus() = PositionF(-x, -y)
    operator fun plus(other: PositionF) = PositionF(x + other.x, y + other.y)
    operator fun minus(other: PositionF) = PositionF(x - other.x, y - other.y)
    operator fun times(size: SizeF) = PositionF(x * size.width, y * size.height)
    operator fun div(size: SizeF) = PositionF(x / size.width, y / size.height)
    operator fun div(value: Float) = PositionF(x / value, y / value)
}