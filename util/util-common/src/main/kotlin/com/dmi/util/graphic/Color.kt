package com.dmi.util.graphic

import com.dmi.util.lang.safeEquals

class Color {
    companion object {
        val TRANSPARENT = Color(0, 0, 0, 0)

        val BLACK = Color(255, 0, 0, 0)
        val WHITE = Color(255, 255, 255, 255)
        val GRAY = Color(255, 127, 127, 127)

        val RED = Color(255, 255, 0, 0)
        val GREEN = Color(255, 0, 255, 0)
        val BLUE = Color(255, 0, 0, 255)

        val CYAN = Color(255, 0, 255, 255)
        val MAGENTA = Color(255, 255, 0, 255)
        val YELLOW = Color(255, 255, 255, 0)
    }


    val value: Int
    val alpha: Int
    val red: Int
    val green: Int
    val blue: Int

    constructor(alpha: Int, red: Int, green: Int, blue: Int) {
        value = alpha shl 24 or (red shl 16) or (green shl 8) or blue
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
    }

    constructor(value: Int) {
        this.value = value
        alpha = value shr 24
        red = value shr 16 and 0xFF
        green = value shr 8 and 0xFF
        blue = value and 0xFF
    }


    override fun equals(other: Any?) = safeEquals(other) { value == it.value }
    override fun hashCode() = value
}