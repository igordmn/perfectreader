package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.SizeF

class Page(
        val column: LayoutColumn,
        val contentSize: SizeF,
        val margins: Margins
) {
    val size = SizeF(contentSize.width + margins.left + margins.right, contentSize.height + margins.top + margins.bottom)
    val range: LocationRange get() = column.range

    class Margins(val left: Float, val right: Float, val top: Float, val bottom: Float)

    override fun toString() = column.toString()
}