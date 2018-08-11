package com.dmi.perfectreader.book.page

import com.dmi.util.graphic.PositionF

interface PageScroller {
    fun scroll(delta: PositionF)
    fun end(velocity: PositionF)
    fun cancel()
}