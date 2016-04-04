package com.dmi.perfectreader.render

import com.dmi.perfectreader.layout.LayoutParagraph

class RenderParagraph(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        val layoutObject: LayoutParagraph
) : RenderObject(width, height, children) {
    override fun canPartiallyPaint() = true
}
