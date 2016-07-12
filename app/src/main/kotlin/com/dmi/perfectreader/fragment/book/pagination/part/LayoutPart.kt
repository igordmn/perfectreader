package com.dmi.perfectreader.fragment.book.pagination.part

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.location.LocationRange

class LayoutPart(
        val obj: LayoutObject,
        val top: Edge,
        val bottom: Edge,
        val range: LocationRange
) {
    val height = bottom.offset - top.offset

    class Edge(val childIndices: List<Int>, val offset: Float)

    override fun toString() = childObject(obj, top.childIndices, 0).toString()

    private fun childObject(obj: LayoutObject, childIndices: List<Int>, level: Int): LayoutObject {
        val child = obj.children[childIndices[level]].obj
        return if (level < childIndices.size - 1) {
            childObject(child, childIndices, level + 1)
        } else {
            child
        }
    }
}