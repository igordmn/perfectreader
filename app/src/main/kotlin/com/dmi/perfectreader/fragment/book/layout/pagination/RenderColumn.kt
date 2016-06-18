package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.LocationRange

class RenderColumn(
        val parts: List<RenderPart> = emptyList(),
        val height: Float,
        val range: LocationRange
)