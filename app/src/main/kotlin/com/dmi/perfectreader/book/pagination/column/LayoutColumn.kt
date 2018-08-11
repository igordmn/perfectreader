package com.dmi.perfectreader.book.pagination.column

import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.pagination.part.LayoutPart

class LayoutColumn(
        val parts: List<LayoutPart> = emptyList(),
        val height: Float,
        val range: LocationRange
) {
    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        var partY = y
        parts.forEach {
            it.forEachChildRecursive(x, partY, action)
            partY += it.height
        }
    }

    override fun toString() = if (parts.size > 0) parts[0].toString() else ""
}