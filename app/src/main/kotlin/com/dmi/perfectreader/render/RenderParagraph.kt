package com.dmi.perfectreader.render

import com.dmi.perfectreader.location.BookRange

class RenderParagraph(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        range: BookRange
) : RenderObject(width, height, children, range) {
    override fun canPartiallyPaint() = true
}
