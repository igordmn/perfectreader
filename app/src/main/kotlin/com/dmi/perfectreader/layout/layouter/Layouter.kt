package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.config.LayoutSpace

interface Layouter<L, R> {
    fun layout(obj: L, space: LayoutSpace): R
}
