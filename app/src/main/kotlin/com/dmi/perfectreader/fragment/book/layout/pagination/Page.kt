package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.graphic.SizeF

class Page(
        val column: RenderColumn,
        val contentSize: SizeF,
        val margins: Margins
) {
    val range: LocationRange get() = column.range

    class Margins(val left: Float, val right: Float, val top: Float, val bottom: Float)
}