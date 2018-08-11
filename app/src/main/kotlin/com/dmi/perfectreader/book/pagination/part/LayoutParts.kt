package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.location.Location
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutChild
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import java.util.*

fun splitIntoParts(rootObj: LayoutObject): List<LayoutPart> {
    val parts = ArrayList<LayoutPart>()

    fun addPartsFrom(obj: LayoutObject, top: Bound, bottom: Bound, absoluteTop: Float, childIndices: List<Int>) {
        val children = obj.children
        if (obj.canBeSeparated() && children.size > 0) {
            children.forEachIndexed { i, child ->
                addPartsFrom(
                        obj = child.obj,
                        top = if (i == 0) top else child.topBound(absoluteTop),
                        bottom = if (i == children.size - 1) bottom else child.bottomBound(absoluteTop),
                        absoluteTop = absoluteTop + child.y,
                        childIndices = childIndices + i
                )
            }
        } else {
            parts.add(LayoutPart(
                    rootObj,
                    LayoutPart.Edge(childIndices, top.offset),
                    LayoutPart.Edge(childIndices, bottom.offset),
                    LocationRange(top.location, bottom.location)
            ))
        }
    }

    addPartsFrom(
            obj = rootObj,
            top = rootObj.topBound(0F),
            bottom = rootObj.bottomBound(0F),
            absoluteTop = 0F,
            childIndices = emptyList()
    )

    return parts
}

private fun LayoutChild.topBound(absoluteTop: Float) = obj.topBound(absoluteTop + y)
private fun LayoutChild.bottomBound(absoluteTop: Float) = obj.bottomBound(absoluteTop + y)

private fun LayoutObject.topBound(absoluteTop: Float) = Bound(absoluteTop + internalMargins().top, range.begin)
private fun LayoutObject.bottomBound(absoluteTop: Float) = Bound(absoluteTop + height - internalMargins().bottom, range.end)

private class Bound(val offset: Float, val location: Location)