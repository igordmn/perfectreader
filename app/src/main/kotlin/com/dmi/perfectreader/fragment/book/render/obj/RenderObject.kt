package com.dmi.perfectreader.fragment.book.render.obj

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject

abstract class RenderObject(val x: Float, val y: Float) {
    abstract val layoutObj: LayoutObject
}