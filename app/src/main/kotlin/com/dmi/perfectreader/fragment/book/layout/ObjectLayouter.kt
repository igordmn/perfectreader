package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.content.obj.ComputedObject
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject

interface ObjectLayouter<L : ComputedObject, R : LayoutObject> {
    fun layout(obj: L, space: LayoutSpace): R
}