package com.dmi.perfectreader.render

class RenderBox(width: Float, height: Float, children: List<RenderChild>) : RenderObject(width, height, children) {
    override fun canPartiallyPainted(): Boolean {
        return true
    }
}
