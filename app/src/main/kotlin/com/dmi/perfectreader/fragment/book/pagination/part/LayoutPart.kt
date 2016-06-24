package com.dmi.perfectreader.fragment.book.pagination.part

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject

class LayoutPart(
        val obj: LayoutObject,
        val top: Edge,
        val bottom: Edge,
        val range: LocationRange
) {
    val height = bottom.offset - top.offset

    class Edge(val childIndices: List<Int>, val offset: Float)
}