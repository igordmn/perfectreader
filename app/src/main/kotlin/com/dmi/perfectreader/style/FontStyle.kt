package com.dmi.perfectreader.style

class FontStyle(
        val size: Float,
        val color: Int,
        val renderParams: RenderParams
) {
    class RenderParams(
            val antialias: Boolean,
            val subpixel: Boolean,
            val hinting: Boolean,
            val linearScaling: Boolean
    )
}
