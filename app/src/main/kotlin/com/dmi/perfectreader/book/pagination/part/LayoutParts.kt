package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutChild
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import java.util.*

fun splitIntoParts(rootObj: LayoutObject): List<LayoutPart> {
    val parts = ArrayList<LayoutPart>()

    fun addPartsFrom(obj: LayoutObject, top: Bound, bottom: Bound, absoluteTop: Float, childIndices: List<Int>, pageBreakBefore: Boolean) {
        val children = obj.children
        if (obj.canBeSeparated() && children.isNotEmpty()) {
            children.forEachIndexed { i, child ->
                addPartsFrom(
                        obj = child.obj,
                        top = if (i == 0) top else child.topBound(absoluteTop),
                        bottom = if (i == children.size - 1) bottom else child.bottomBound(absoluteTop),
                        absoluteTop = absoluteTop + child.y,
                        childIndices = childIndices + i,
                        pageBreakBefore = i == 0 && pageBreakBefore || obj.pageBreakBefore
                )
            }
        } else {
            parts.add(LayoutPart(
                    rootObj,
                    LayoutPart.Edge(childIndices, top.offset),
                    LayoutPart.Edge(childIndices, bottom.offset),
                    LocationRange(top.location, bottom.location),
                    pageBreakBefore
            ))
        }
    }

    addPartsFrom(
            obj = rootObj,
            top = rootObj.topBound(0F),
            bottom = rootObj.bottomBound(0F),
            absoluteTop = 0F,
            childIndices = emptyList(),
            pageBreakBefore = rootObj.pageBreakBefore
    )

    return parts
}

private fun LayoutChild.topBound(absoluteTop: Float) = obj.topBound(absoluteTop + y)
private fun LayoutChild.bottomBound(absoluteTop: Float) = obj.bottomBound(absoluteTop + y)

private fun LayoutObject.topBound(absoluteTop: Float) = Bound(absoluteTop + internalMargins().top, range.start)
private fun LayoutObject.bottomBound(absoluteTop: Float) = Bound(absoluteTop + height - internalMargins().bottom, range.endInclusive)

private class Bound(val offset: Float, val location: Location)