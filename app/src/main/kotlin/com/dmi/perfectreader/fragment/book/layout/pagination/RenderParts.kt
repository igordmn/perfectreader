package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.render.RenderChild
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import java.util.*

fun splitIntoParts(rootObj: RenderObject): List<RenderPart> {
    val parts = ArrayList<RenderPart>()

    fun addPartsFrom(obj: RenderObject, top: Bound, bottom: Bound, absoluteTop: Float, childIndices: List<Int>) {
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
            parts.add(RenderPart(
                    rootObj,
                    RenderPart.Edge(childIndices, top.offset),
                    RenderPart.Edge(childIndices, bottom.offset),
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

private fun RenderChild.topBound(absoluteTop: Float) = obj.topBound(absoluteTop + y)
private fun RenderChild.bottomBound(absoluteTop: Float) = obj.bottomBound(absoluteTop + y)

private fun RenderObject.topBound(absoluteTop: Float) = Bound(absoluteTop + internalMargins().top, range.begin)
private fun RenderObject.bottomBound(absoluteTop: Float) = Bound(absoluteTop + height - internalMargins().bottom, range.end)

private class Bound(val offset: Float, val location: Location)