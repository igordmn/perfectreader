package com.dmi.util.graphic

data class Rect(val left: Int, val right: Int, val top: Int, val bottom: Int) {
    val isEmpty: Boolean = right <= left && bottom <= top
}