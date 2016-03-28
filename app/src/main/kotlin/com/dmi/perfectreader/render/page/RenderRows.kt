package com.dmi.perfectreader.render.page

import com.dmi.perfectreader.render.RenderChild
import com.dmi.perfectreader.render.RenderObject
import com.dmi.perfectreader.render.page.RenderRow.Edge
import java.util.*

fun splitIntoRows(rootObj: RenderObject): List<RenderRow> {
    val rows = ArrayList<RenderRow>()

    fun RenderChild.topOffset(absoluteTop: Float) =
            absoluteTop + y + obj.internalMargins().top

    fun RenderChild.bottomOffset(absoluteTop: Float) =
            absoluteTop + y + obj.height - obj.internalMargins().bottom

    fun addRowsFrom(obj: RenderObject, rowTop: Float, rowBottom: Float, absoluteTop: Float, childIndices: List<Int>) {
        val children = obj.children
        if (obj.canPartiallyPaint() && children.size > 0) {
            for (i in 0..children.size - 1) {
                val child = children[i]
                addRowsFrom(
                        obj = child.obj,
                        rowTop = if (i == 0) rowTop else child.topOffset(absoluteTop),
                        rowBottom = if (i == children.size - 1) rowBottom else child.bottomOffset(absoluteTop),
                        absoluteTop = absoluteTop + child.y,
                        childIndices = childIndices + i
                )
            }
        } else {
            rows.add(RenderRow(
                    rootObj,
                    Edge(childIndices, rowTop),
                    Edge(childIndices, rowBottom)
            ))
        }
    }

    addRowsFrom(
            obj = rootObj,
            rowTop = rootObj.internalMargins().top,
            rowBottom = rootObj.height - rootObj.internalMargins().bottom,
            absoluteTop = 0F,
            childIndices = emptyList()
    )

    return rows
}
