package com.dmi.perfectreader.layout.common

import com.dmi.perfectreader.layout.common.LayoutSpace

interface Layouter<L, R> {
    fun layout(obj: L, space: LayoutSpace): R
}
