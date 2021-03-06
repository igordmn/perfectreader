package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutObject

class LayoutPart(
        val obj: LayoutObject,
        val top: Edge,
        val bottom: Edge,
        val range: LocationRange,
        val pageBreakBefore: Boolean = false
) {
    val height = bottom.offset - top.offset

    class Edge(val childIndices: List<Int>, val offset: Float) {
        fun childIndex(level: Int, isEdge: Boolean, defaultIndex: Int): Int {
            return if (isEdge) {
                if (level < childIndices.size) childIndices[level] else defaultIndex
            } else {
                defaultIndex
            }
        }
    }

    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        forEachChildRecursive(x, y - top.offset, action, obj, 0, true, true)
    }

    private fun forEachChildRecursive(
            x: Float,
            y: Float,
            action: (x: Float, y: Float, obj: LayoutObject) -> Unit,
            obj: LayoutObject,
            level: Int,
            isFirstBranch: Boolean,
            isLastBranch: Boolean
    ) {
        action(x, y, obj)

        val children = obj.children
        if (children.isNotEmpty()) {
            val firstChildIndex = top.childIndex(level, isFirstBranch, 0)
            val lastChildIndex = bottom.childIndex(level, isLastBranch, children.size - 1)

            for (i in firstChildIndex..lastChildIndex) {
                val child = children[i]
                forEachChildRecursive(
                        x + child.x,
                        y + child.y,
                        action,
                        child.obj,
                        level + 1,
                        isFirstBranch && i == firstChildIndex,
                        isLastBranch && i == lastChildIndex
                )
            }
        }
    }

    override fun toString() = if (obj.children.isNotEmpty()) childObject(obj, top.childIndices, 0).toString() else "-"

    private fun childObject(obj: LayoutObject, childIndices: List<Int>, level: Int): LayoutObject {
        val child = obj.children[childIndices[level]].obj
        return if (level < childIndices.size - 1) {
            childObject(child, childIndices, level + 1)
        } else {
            child
        }
    }
}