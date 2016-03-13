package com.dmi.perfectreader.layout.config

class LayoutContext(val parentSize: Size, val areaSize: Size) {
    companion object {
        fun root(width: Float, height: Float) = LayoutContext(Size(width, height), Size(width, height))
    }

    class Size(val width: Float, val height: Float)
}
