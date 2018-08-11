package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.book.pagination.part.LayoutPart
import com.dmi.util.graphic.SizeF

@JvmName("testPage1")
fun testPage(offsetRange: ClosedRange<Double>): Page = testPage(Location(offsetRange.start)..Location(offsetRange.endInclusive))

@JvmName("testPage2")
fun testPage(range: LocationRange): Page = testPage(object : LayoutObject(10F, 10F, emptyList(), range) {})

fun testPage(obj: LayoutObject): Page {
    val part = LayoutPart(obj, LayoutPart.Edge(listOf(0), 0F), LayoutPart.Edge(listOf(obj.children.size - 1), obj.height), obj.range)
    val column = LayoutColumn(listOf(part), obj.height, obj.range)
    return Page(column, SizeF(obj.width, obj.height), Page.Paddings(0F, 0F, 0F, 0F), 1F)
}