package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.SizeF

class Page(
        val column: LayoutColumn,
        val contentSize: SizeF,
        val margins: Margins,
        val textGammaCorrection: Float
) {
    val size = SizeF(contentSize.width + margins.left + margins.right, contentSize.height + margins.top + margins.bottom)
    val range: LocationRange get() = column.range

    class Margins(val left: Float, val right: Float, val top: Float, val bottom: Float)

    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        column.forEachChildRecursive(x + margins.left, y + margins.top, action)
    }

    override fun toString() = column.toString()
}