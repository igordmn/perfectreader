package com.dmi.perfectreader.render

import com.dmi.perfectreader.layout.LayoutBox

class RenderBox(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        val layoutObject: LayoutBox
) : RenderObject(width, height, children) {
    override fun canPartiallyPaint() = true
}
