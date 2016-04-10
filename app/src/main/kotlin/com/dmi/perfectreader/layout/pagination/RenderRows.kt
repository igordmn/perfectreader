package com.dmi.perfectreader.layout.pagination

import com.dmi.perfectreader.layout.pagination.RenderRow.Edge
import com.dmi.perfectreader.layout.renderobj.RenderChild
import com.dmi.perfectreader.layout.renderobj.RenderObject
import com.dmi.perfectreader.location.BookLocation
import com.dmi.perfectreader.location.BookRange
import java.util.*

fun splitIntoRows(rootObj: RenderObject): List<RenderRow> {
    val rows = ArrayList<RenderRow>()

    fun addRowsFrom(obj: RenderObject, top: Bound, bottom: Bound, absoluteTop: Float, childIndices: List<Int>) {
        val children = obj.children
        if (obj.canPartiallyPaint() && children.size > 0) {
            for (i in 0..children.size - 1) {
                val child = children[i]
                addRowsFrom(
                        obj = child.obj,
                        top = if (i == 0) top else child.topBound(absoluteTop),
                        bottom = if (i == children.size - 1) bottom else child.bottomBound(absoluteTop),
                        absoluteTop = absoluteTop + child.y,
                        childIndices = childIndices + i
                )
            }
        } else {
            rows.add(RenderRow(
                    rootObj,
                    Edge(childIndices, top.offset),
                    Edge(childIndices, bottom.offset),
                    BookRange(top.location, bottom.location)
            ))
        }
    }

    addRowsFrom(
            obj = rootObj,
            top = rootObj.topBound(0F),
            bottom = rootObj.bottomBound(0F),
            absoluteTop = 0F,
            childIndices = emptyList()
    )

    return rows
}

private fun RenderChild.topBound(absoluteTop: Float) = obj.topBound(absoluteTop + y)
private fun RenderChild.bottomBound(absoluteTop: Float) = obj.bottomBound(absoluteTop + y)

private fun RenderObject.topBound(absoluteTop: Float) = Bound(absoluteTop + internalMargins().top, range.begin)
private fun RenderObject.bottomBound(absoluteTop: Float) = Bound(absoluteTop + height - internalMargins().bottom, range.end)

fun singleRowPage(row: RenderRow) = RenderPage(listOf(row), row.height, row.range)

infix fun RenderPage.merge(row: RenderRow): RenderPage {
    val rows = if (this.rows.size == 0) {
        listOf(row)
    } else {
        val last = this.rows.last()
        if (last.obj == row.obj) {
            this.rows.dropLast(1) + listOf(last merge row)
        } else {
            this.rows.dropLast(1) + listOf(last.extendToEnd(), row.extendToBegin())
        }
    }
    return RenderPage(rows, heightSumOf(rows), BookRange(range.begin, row.range.end))
}

infix fun RenderRow.merge(page: RenderPage): RenderPage {
    val rows = if (page.rows.size == 0) {
        listOf(this)
    } else {
        val first = page.rows.first()
        if (first.obj == obj) {
            listOf(this merge first) + page.rows.drop(1)
        } else {
            listOf(this.extendToEnd(), first.extendToBegin()) + page.rows.drop(1)
        }
    }
    return RenderPage(rows, heightSumOf(rows), BookRange(range.begin, page.range.end))
}

private infix fun RenderRow.merge(other: RenderRow) = RenderRow(
        obj,
        top,
        other.bottom,
        BookRange(range.begin, other.range.end)
)

private fun RenderRow.extendToEnd() = RenderRow(
        obj,
        top,
        RenderRow.Edge(bottom.childIndices, obj.height),
        BookRange(range.begin, obj.range.end)
)

private fun RenderRow.extendToBegin() = RenderRow(
        obj,
        RenderRow.Edge(top.childIndices, 0F),
        bottom,
        BookRange(obj.range.begin, range.end)
)

private fun heightSumOf(rows: List<RenderRow>): Float {
    var sum = 0F
    for (row in rows) {
        sum += row.height
    }
    return sum
}

private class Bound(val offset: Float, val location: BookLocation)
