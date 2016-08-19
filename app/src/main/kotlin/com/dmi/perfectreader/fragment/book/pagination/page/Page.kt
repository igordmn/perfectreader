package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.SizeF

class Page(
        val column: LayoutColumn,
        val size: SizeF,
        val paddings: Paddings,
        val textGammaCorrection: Float
) {
    val range: LocationRange get() = column.range

    class Paddings(val left: Float, val right: Float, val top: Float, val bottom: Float) {
        operator fun times(value: Float) = Paddings(left * value, right * value, top * value, bottom * value)
    }

    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        column.forEachChildRecursive(x + paddings.left, y + paddings.top, action)
    }

    override fun toString() = column.toString()
}