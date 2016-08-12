package com.dmi.perfectreader.fragment.book.selection

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.SelectionConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.TextRenderConfig
import com.dmi.perfectreader.fragment.book.layout.obj.*
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart
import com.dmi.test.shouldEqual
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.SizeF
import org.junit.Ignore
import org.junit.Test
import java.util.*

@Ignore
class SelectionUtilsTest {
    @Test
    fun clickableObjectAtPosition() {
        // given
        val range = range(0, 30)

        val obj1 = clickable(50F, 8F, range)
        val obj2 = clickable(20F, 8F, range)
        val obj3 = clickable(50F, 8F, range)
        val page = page(
                LayoutBox(100F, 100F, listOf(
                        childLine(
                                11F, 10F, 80F, 10F, range,
                                LayoutChild(16F - 11F, 10F - 10F, obj1),
                                LayoutChild(56F - 11F, 10F - 10F, obj2)   // пересекает предыдущий на 10 px
                        ),
                        childLine(
                                12F, 20F, 80F, 10F, range,
                                LayoutChild(17F - 12F, 20F - 20F, obj3)
                        )
                ), range)
        )

        // expect
        clickableObjectAt(page, 0F, 0F, 0F) shouldEqual null
        clickableObjectAt(page, 11F, 10F, 0F) shouldEqual null
        clickableObjectAt(page, 16F, 10F, 0F) shouldEqual obj1
        clickableObjectAt(page, 56F, 10F, 0F) shouldEqual obj2
        clickableObjectAt(page, 56F + 20F, 10F, 0F) shouldEqual obj2
        clickableObjectAt(page, 56F + 20.1F, 10F, 0F) shouldEqual null
        clickableObjectAt(page, 17F - 1F, 20F, 0F) shouldEqual null
        clickableObjectAt(page, 17F, 20F, 0F) shouldEqual obj3
        clickableObjectAt(page, 17F + 50F, 20F, 0F) shouldEqual obj3
        clickableObjectAt(page, 17F + 51F, 20F, 0F) shouldEqual null
        clickableObjectAt(page, 17F + 510F, 20F, 0F) shouldEqual null
        clickableObjectAt(page, 17F, 20F + 8F, 0F) shouldEqual obj3
        clickableObjectAt(page, 17F, 20F + 8.1F, 0F) shouldEqual null

        val squareOfTwo = (Math.sqrt(2.0) + 0.01).toFloat()
        clickableObjectAt(page, 16F - 1F, 10F - 1F, squareOfTwo - 0.1F) shouldEqual null
        clickableObjectAt(page, 16F - 1F, 10F - 1F, squareOfTwo) shouldEqual obj1
        clickableObjectAt(page, 56F + 21F, 10F, 1F) shouldEqual obj2
        clickableObjectAt(page, 56F + 21.1F, 10F, 1F) shouldEqual null
        clickableObjectAt(page, 17F - 1.1F, 20F, 1F) shouldEqual null
        clickableObjectAt(page, 17F - 1F, 20F, 1F) shouldEqual obj3
        clickableObjectAt(page, 17F + 51F, 20F, 1F) shouldEqual obj3
        clickableObjectAt(page, 17F + 51.1F, 20F, 1F) shouldEqual null
        clickableObjectAt(page, 17F, 20F + 9F, 1F) shouldEqual obj3
        clickableObjectAt(page, 17F, 20F + 9.1F, 1F) shouldEqual null
    }

    fun childLine(
            x: Float, y: Float, width: Float, height: Float, range: LocationRange,
            child1: LayoutChild? = null, child2: LayoutChild? = null, child3: LayoutChild? = null
    ): LayoutChild {
        val children = ArrayList<LayoutChild>()
        if (child1 != null)
            children.add(child1)
        if (child2 != null)
            children.add(child2)
        if (child3 != null)
            children.add(child3)
        return LayoutChild(x, y, LayoutLine(width, height, children, range))
    }

    fun clickable(width: Float, height: Float, range: LocationRange) = object : LayoutObject(width, height, listOf(), range) {
        override fun isClickable() = true
    }

    fun text(width: Float, height: Float, charOffsets: FloatArray, range: LocationRange) = LayoutText(
            width, height, textString(charOffsets.size), Locale.US, 4F, charOffsets, style(), range
    )

    fun textString(length: Int) = (1..length).map { 'a' }.joinToString("")

    fun style() = ConfiguredFontStyle(12F, Color.RED, TextRenderConfig(true, true, true, true), SelectionConfig(Color.BLACK))

    fun location(offset: Int) = Location(offset.toDouble())
    fun range(begin: Int, end: Int) = LocationRange(location(begin), location(end))

    fun page(obj: LayoutObject): Page {
        val part = LayoutPart(obj, LayoutPart.Edge(listOf(0), 0F), LayoutPart.Edge(listOf(obj.children.size - 1), obj.height), obj.range)
        val column = LayoutColumn(listOf(part), obj.height, obj.range)
        return Page(column, SizeF(obj.width, obj.height), Page.Margins(0F, 0F, 0F, 0F))
    }
}