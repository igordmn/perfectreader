package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject

interface Layouter<L : ComputedObject, R : LayoutObject> {
    fun layout(obj: L, space: LayoutSpace): R
}