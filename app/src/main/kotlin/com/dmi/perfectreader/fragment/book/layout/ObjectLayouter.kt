package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject

interface ObjectLayouter<L : ConfiguredObject, R : LayoutObject> {
    fun layout(obj: L, space: LayoutSpace): R
}