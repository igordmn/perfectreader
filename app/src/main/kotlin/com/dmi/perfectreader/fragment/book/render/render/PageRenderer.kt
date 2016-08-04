package com.dmi.perfectreader.fragment.book.render.render

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage
import java.util.*

class PageRenderer(private val objectRenderer: UniversalObjectRenderer) {
    fun render(page: Page): RenderPage {
        val objects = ArrayList<RenderObject>()
        renderColumn(page.margins.left, page.margins.top, page.column, objects)
        return RenderPage(objects, page)
    }

    fun renderColumn(x: Float, y: Float, column: LayoutColumn, objects: ArrayList<RenderObject>) {
        var partY = y
        column.parts.forEach {
            renderPart(x, partY, it, objects)
            partY += it.height
        }
    }

    fun renderPart(x: Float, y: Float, part: LayoutPart, objects: ArrayList<RenderObject>) {
        renderObjectsRecursive(x, y - part.top.offset, part, part.obj, 0, true, true, objects)
    }

    private fun renderObjectsRecursive(
            x: Float,
            y: Float,
            part: LayoutPart,
            obj: LayoutObject,
            level: Int,
            isFirstBranch: Boolean,
            isLastBranch: Boolean,
            objects: ArrayList<RenderObject>
    ) {
        objectRenderer.render(x, y, obj, objects)

        val children = obj.children
        if (children.size > 0) {
            val firstChildIndex = part.top.childIndex(level, isFirstBranch, 0)
            val lastChildIndex = part.bottom.childIndex(level, isLastBranch, children.size - 1)

            for (i in firstChildIndex..lastChildIndex) {
                val child = children[i]
                renderObjectsRecursive(
                        x + child.x,
                        y + child.y,
                        part,
                        child.obj,
                        level + 1,
                        isFirstBranch && i == firstChildIndex,
                        isLastBranch && i == lastChildIndex,
                        objects
                )
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