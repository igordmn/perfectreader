package com.dmi.perfectreader.book.pagination.column

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.pagination.part.LayoutPart

fun singlePartColumn(part: LayoutPart) = LayoutColumn(listOf(part), part.height, part.range)

infix fun LayoutColumn.merge(part: LayoutPart): LayoutColumn {
    val last = this.parts.lastOrNull()
    val parts = if (last == null) {
        listOf(part)
    } else if (last.obj == part.obj) {
        this.parts.dropLast(1) + listOf(last merge part)
    } else {
        this.parts.dropLast(1) + listOf(last.extendToEnd(), part.extendToBegin())
    }
    return LayoutColumn(parts, heightSumOf(parts), LocationRange(range.start, part.range.endInclusive))
}

infix fun LayoutPart.merge(column: LayoutColumn): LayoutColumn {
    val first = column.parts.firstOrNull()
    val parts = if (first == null) {
        listOf(this)
    } else if (first.obj == obj) {
        listOf(this merge first) + column.parts.drop(1)
    } else {
        listOf(this.extendToEnd(), first.extendToBegin()) + column.parts.drop(1)
    }
    return LayoutColumn(parts, heightSumOf(parts), LocationRange(range.start, column.range.endInclusive))
}

private infix fun LayoutPart.merge(other: LayoutPart) = LayoutPart(
        obj,
        top,
        other.bottom,
        LocationRange(range.start, other.range.endInclusive)
)

private fun LayoutPart.extendToEnd() = LayoutPart(
        obj,
        top,
        LayoutPart.Edge(bottom.childIndices, obj.height),
        LocationRange(range.start, obj.range.endInclusive)
)

private fun LayoutPart.extendToBegin() = LayoutPart(
        obj,
        LayoutPart.Edge(top.childIndices, 0F),
        bottom,
        LocationRange(obj.range.start, range.endInclusive)
)

private fun heightSumOf(parts: List<LayoutPart>): Float {
    var sum = 0F
    for (part in parts) {
        sum += part.height
    }
    return sum
}