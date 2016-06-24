package com.dmi.perfectreader.fragment.book.pagination.column

import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart
import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutColumn(
        val parts: List<LayoutPart> = emptyList(),
        val height: Float,
        val range: LocationRange
)