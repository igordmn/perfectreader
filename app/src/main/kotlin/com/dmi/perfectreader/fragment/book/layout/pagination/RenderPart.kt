package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject

class RenderPart(
        val obj: RenderObject,
        val top: Edge,
        val bottom: Edge,
        val range: LocationRange
) {
    val height = bottom.offset - top.offset

    class Edge(val childIndices: List<Int>, val offset: Float)
}