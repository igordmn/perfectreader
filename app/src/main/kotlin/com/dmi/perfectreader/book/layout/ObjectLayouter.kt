package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.content.configure.ConfiguredObject
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.LayoutObject

interface ObjectLayouter<L : ConfiguredObject, R : LayoutObject> {
    fun layout(obj: L, space: LayoutSpace): R
}