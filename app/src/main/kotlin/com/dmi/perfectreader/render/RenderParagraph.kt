package com.dmi.perfectreader.render

class RenderParagraph(width: Float, height: Float, children: List<RenderChild>) : RenderObject(width, height, children) {
    override fun canPartiallyPainted() = true
}
