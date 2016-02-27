package com.dmi.perfectreader.style

class FontStyle(private val size: Float, private val color: Int, private val renderParams: FontStyle.RenderParams) {

    fun size(): Float {
        return size
    }

    fun color(): Int {
        return color
    }

    fun renderParams(): RenderParams {
        return renderParams
    }

    class RenderParams(private val antialias: Boolean, private val subpixel: Boolean, private val hinting: Boolean, private val linearScaling: Boolean) {

        fun textAntialias(): Boolean {
            return antialias
        }

        fun textSubpixel(): Boolean {
            return subpixel
        }

        fun textHinting(): Boolean {
            return hinting
        }

        fun textLinearScaling(): Boolean {
            return linearScaling
        }
    }
}
