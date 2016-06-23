package com.dmi.perfectreader.fragment.book.layout.pagination

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.painter.ObjectPainter
import com.dmi.perfectreader.fragment.book.layout.painter.PaintContext
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject

class RenderPartPainter(private val objectPainter: ObjectPainter<RenderObject>) {
    fun paint(part: RenderPart, canvas: Canvas, context: PaintContext) {
        with (part) {
            canvas.save()

            canvas.translate(0F, -top.offset)
            paintRecursive(part, canvas, context, obj, 0, true, true)

            canvas.restore()
        }
    }

    private fun paintRecursive(
            part: RenderPart,
            canvas: Canvas,
            context: PaintContext,
            obj: RenderObject,
            level: Int,
            isFirstBranch: Boolean,
            isLastBranch: Boolean
    ) {
        objectPainter.paintItself(obj, canvas, context)

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

    private fun RenderPart.Edge.childIndex(level: Int, isEdge: Boolean, defaultIndex: Int) =
            if (isEdge) {
                if (level < childIndices.size) childIndices[level] else defaultIndex
            } else {
                defaultIndex
            }
}