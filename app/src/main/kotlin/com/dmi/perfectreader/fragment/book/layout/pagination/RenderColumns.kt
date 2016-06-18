package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.LocationRange

fun singlePartColumn(part: RenderPart) = RenderColumn(listOf(part), part.height, part.range)

infix fun RenderColumn.merge(part: RenderPart): RenderColumn {
    val parts = if (this.parts.size == 0) {
        listOf(part)
    } else {
        val last = this.parts.last()
        if (last.obj == part.obj) {
            this.parts.dropLast(1) + listOf(last merge part)
        } else {
            this.parts.dropLast(1) + listOf(last.extendToEnd(), part.extendToBegin())
        }
    }
    return RenderColumn(parts, heightSumOf(parts), LocationRange(range.begin, part.range.end))
}

infix fun RenderPart.merge(column: RenderColumn): RenderColumn {
    val parts = if (column.parts.size == 0) {
        listOf(this)
    } else {
        val first = column.parts.first()
        if (first.obj == obj) {
            listOf(this merge first) + column.parts.drop(1)
        } else {
            listOf(this.extendToEnd(), first.extendToBegin()) + column.parts.drop(1)
        }
    }
    return RenderColumn(parts, heightSumOf(parts), LocationRange(range.begin, column.range.end))
}

private infix fun RenderPart.merge(other: RenderPart) = RenderPart(
        obj,
        top,
        other.bottom,
        LocationRange(range.begin, other.range.end)
)

private fun RenderPart.extendToEnd() = RenderPart(
        obj,
        top,
        RenderPart.Edge(bottom.childIndices, obj.height),
        LocationRange(range.begin, obj.range.end)
)

private fun RenderPart.extendToBegin() = RenderPart(
        obj,
        RenderPart.Edge(top.childIndices, 0F),
        bottom,
        LocationRange(obj.range.begin, range.end)
)

private fun heightSumOf(parts: List<RenderPart>): Float {
    var sum = 0F
    for (part in parts) {
        sum += part.height
    }
    return sum
}
