package com.dmi.perfectreader.layout.common

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.render.RenderObject

interface Layouter<L : LayoutObject, R : RenderObject> {
    fun layout(obj: L, space: LayoutSpace): R
}
