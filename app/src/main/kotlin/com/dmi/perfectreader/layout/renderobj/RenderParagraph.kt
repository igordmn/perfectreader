package com.dmi.perfectreader.layout.renderobj

import com.dmi.perfectreader.location.BookRange

class RenderParagraph(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        range: BookRange
) : RenderObject(width, height, children, range) {
    override fun canPartiallyPaint() = true
}