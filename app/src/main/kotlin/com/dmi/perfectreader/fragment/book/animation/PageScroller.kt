package com.dmi.perfectreader.fragment.book.animation

import com.dmi.util.graphic.PositionF

interface PageScroller {
    fun scroll(delta: PositionF)
    fun end(velocity: PositionF)
    fun cancel()
}