package com.dmi.perfectreader.book.pagination.page

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF

class Page(
        val column: LayoutColumn,
        val footer: LayoutObject?,
        val size: SizeF,
        val contentPosition: PositionF,
        val footerPosition: PositionF,
        val textGammaCorrection: Float
) {
    val range: LocationRange get() = column.range
    val isNextContinuous: Boolean get() = column.isNextContinuous

    class Paddings(val left: Float, val right: Float, val top: Float, val bottom: Float) {
        operator fun times(value: Float) = Paddings(left * value, right * value, top * value, bottom * value)
    }

    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        column.forEachChildRecursive(x + contentPosition.x, y + contentPosition.y, action)
        footer?.forEachChildRecursive(x + footerPosition.x, y + footerPosition.y, action)
    }

    fun forEachColumnChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        column.forEachChildRecursive(x + contentPosition.x, y + contentPosition.y, action)
    }

    override fun toString() = column.toString()
}