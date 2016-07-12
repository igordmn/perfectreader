package com.dmi.perfectreader.fragment.book.pagination.column

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart

class LayoutColumn(
        val parts: List<LayoutPart> = emptyList(),
        val height: Float,
        val range: LocationRange
) {
    override fun toString() = if (parts.size > 0) parts[0].toString() else ""
}