package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart

class PartPainter(private val objectPainter: ObjectPainter<LayoutObject>) {
    fun paint(part: LayoutPart, context: PageContext, canvas: Canvas) {
        with (part) {
            canvas.save()

            canvas.translate(0F, -top.offset)
            paintRecursive(part, canvas, context, obj, 0, true, true)

            canvas.restore()
        }
    }

    private fun paintRecursive(
            part: LayoutPart,
            canvas: Canvas,
            context: PageContext,
            obj: LayoutObject,
            level: Int,
            isFirstBranch: Boolean,
            isLastBranch: Boolean
    ) {
        objectPainter.paintItself(obj, context, canvas)

        val children = obj.children
        if (children.size > 0) {
            val firstChildIndex = part.top.childIndex(level, isFirstBranch, 0)
            val lastChildIndex = part.bottom.childIndex(level, isLastBranch, children.size - 1)

            for (i in firstChildIndex..lastChildIndex) {
                val child = children[i]
                canvas.translate(child.x, child.y)
                paintRecursive(
                        part,
                        canvas,
                        context,
                        child.obj,
                        level + 1,
                        isFirstBranch && i == firstChildIndex,
                        isLastBranch && i == lastChildIndex
                )
                canvas.translate(-child.x, -child.y)
            }
        }
    }

    private fun LayoutPart.Edge.childIndex(level: Int, isEdge: Boolean, defaultIndex: Int) =
            if (isEdge) {
                if (level < childIndices.size) childIndices[level] else defaultIndex
            } else {
                defaultIndex
            }
}