package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.config.LayoutArea

interface Layouter<L, R> {
    fun layout(obj: L, area: LayoutArea): R
}
