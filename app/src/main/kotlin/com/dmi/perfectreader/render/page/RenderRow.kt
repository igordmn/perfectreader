package com.dmi.perfectreader.render.page

import android.graphics.Canvas
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.render.RenderObject

class RenderRow(
        val obj: RenderObject,
        val top: Edge,
        val bottom: Edge,
        val range: BookRange
) {
    val height = bottom.offset - top.offset

    fun paint(canvas: Canvas) {
        canvas.save()

        canvas.clipRect(0F, top.offset, Float.MAX_VALUE, bottom.offset)
        canvas.translate(0F, -top.offset)
        paintRecursive(canvas, obj, 0, true, true)

        canvas.restore()
    }

    private fun paintRecursive(canvas: Canvas, obj: RenderObject, level: Int, isFirstBranch: Boolean, isLastBranch: Boolean) {
        obj.paintItself(canvas)

        val children = obj.children
        if (children.size > 0) {
            val firstChildIndex = top.childIndex(level, isFirstBranch, 0)
            val lastChildIndex = bottom.childIndex(level, isLastBranch, children.size - 1)

            for (i in firstChildIndex..lastChildIndex) {
                val child = children[i]
                canvas.translate(child.x, child.y)
                paintRecursive(
                        canvas,
                        child.obj,
                        level + 1,
                        isFirstBranch && i == firstChildIndex,
                        isLastBranch && i == lastChildIndex
                )
                canvas.translate(-child.x, -child.y)
            }
        }
    }

    private fun Edge.childIndex(level: Int, isEdge: Boolean, defaultIndex: Int) =
            if (isEdge) {
                if (level < childIndices.size) childIndices[level] else defaultIndex
            } else {
                defaultIndex
            }

    class Edge(val childIndices: List<Int>, val offset: Float)
}
