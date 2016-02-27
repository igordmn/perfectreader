package com.dmi.perfectreader.render

class RenderChild(val x: Float, val y: Float, val `object`: RenderObject) {

    fun x(): Float {
        return x
    }

    fun y(): Float {
        return y
    }

    fun `object`(): RenderObject {
        return `object`
    }
}
