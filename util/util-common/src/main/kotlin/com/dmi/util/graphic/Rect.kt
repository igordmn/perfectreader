package com.dmi.util.graphic

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    val isEmpty: Boolean = right <= left && bottom <= top
}