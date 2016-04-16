package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.layout.layoutobj.LayoutObject
import com.dmi.perfectreader.layout.renderobj.RenderObject

interface Layouter<L : LayoutObject, R : RenderObject> {
    fun layout(obj: L, space: LayoutSpace): R
}