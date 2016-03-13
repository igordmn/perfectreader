package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.config.LayoutContext

interface Layouter<L, R> {
    fun layout(obj: L, context: LayoutContext): R
}
