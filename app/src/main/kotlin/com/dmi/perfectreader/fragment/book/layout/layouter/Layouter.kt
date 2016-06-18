package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject

interface Layouter<L : LayoutObject, R : RenderObject> {
    fun layout(obj: L, space: LayoutSpace): R
}