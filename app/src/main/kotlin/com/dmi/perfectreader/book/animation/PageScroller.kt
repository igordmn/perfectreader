package com.dmi.perfectreader.book.animation

import com.dmi.util.graphic.PositionF

interface PageScroller {
    fun scroll(delta: PositionF)
    fun end(velocity: PositionF)
    fun cancel()
}