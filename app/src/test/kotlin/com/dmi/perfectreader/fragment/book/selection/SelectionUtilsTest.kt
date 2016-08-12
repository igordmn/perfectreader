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
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import org.junit.Ignore
import org.junit.Test
import java.lang.Math.round
import java.util.*

@Ignore
class SelectionUtilsTest {
    @Test
    fun selectionLocationNearestToPosition() {
        // given
        val charOffsets = floatArrayOf(1F, 2F, 3F, 7F)

        val text1 = text(50F, 8F, charOffsets, range(0, 30))
        val text2 = text(20F, 8F, charOffsets, range(30, 50))
        val text3 = text(50F, 8F, charOffsets, range(50, 80))
        val text4 = text(50F, 8F, charOffsets, range(90, 94))
        val page = page(
                LayoutBox(100F, 100F, listOf(
                        childLine(
                                11F, 10F, 80F, 10F, range(0, 50),
                                LayoutChild(16F - 11F, 10F - 10F, text1),
                                LayoutChild(56F - 11F, 10F - 10F, text2)   // пересекает предыдущий на 10 px
                        ),
                        childLine(
                                12F, 20F, 80F, 10F, range(50, 100),
                                LayoutChild(17F - 12F, 20F - 20F, text3),
                                LayoutChild(70F - 12F, 20F - 20F, text4)
                        )
                ), range(0, 100))
        )

        // expect

        // text1:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        selectionLocationNearestTo(page, -10F, -10F, Location(1000.0)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, -10F, -10F, Location(7.5)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, -10F, -10F, Location(3.75)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, -10F, -10F, Location(3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, -10F, -10F, Location(0.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, -10F, -10F, Location(-1000.0)) shouldEqual Location(7.5)

        selectionLocationNearestTo(page, 17F, 10F, Location(1000.0)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, 10F, Location(7.5)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, 10F, Location(3.75)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, 10F, Location(3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17F, 10F, Location(0.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17F, 10F, Location(-1000.0)) shouldEqual Location(7.5)

        selectionLocationNearestTo(page, 17F, -10F, Location(1000.0)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, -10F, Location(7.5)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, -10F, Location(3.75)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17F, -10F, Location(3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17F, -10F, Location(0.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17F, -10F, Location(-1000.0)) shouldEqual Location(7.5)

        selectionLocationNearestTo(page, 17.49F, 10F, Location(1000.0)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17.49F, 10F, Location(7.5)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17.49F, 10F, Location(3.75)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17.49F, 10F, Location(3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.49F, 10F, Location(0.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.49F, 10F, Location(-1000.0)) shouldEqual Location(7.5)

        selectionLocationNearestTo(page, 17.5F, 10F, Location(1000.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(15.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(7.5 + 3.75)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(7.5 + 3.74)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(3.75)) shouldEqual Location(0.0)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 17.5F, 10F, Location(-1000.0)) shouldEqual Location(7.5)

        selectionLocationNearestTo(page, 18.5F, 10F, Location(1000.0)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(22.5)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(15.0 + 3.75)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(15.0 + 3.74)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(7.5 + 3.75)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(7.5 + 3.74)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 18.5F, 10F, Location(-1000.0)) shouldEqual Location(15.0)


        // text1:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        // text2:
        // x                                57    58    59    63    83
        // text                             |  a  |  b  |  c  |  d  |
        // loc                              30    35    40    45    50

        selectionLocationNearestTo(page, 26F, 10F, Location(1000.0)) shouldEqual Location(22.5)
        selectionLocationNearestTo(page, 26F, 10F, Location(30.0 - 3.75)) shouldEqual Location(22.5)
        selectionLocationNearestTo(page, 26F, 10F, Location(30.0 - 3.76)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 26F, 10F, Location(22.5 - 3.75)) shouldEqual Location(15.0)
        selectionLocationNearestTo(page, 26F, 10F, Location(22.5 - 3.76)) shouldEqual Location(22.5)
        selectionLocationNearestTo(page, 26F, 10F, Location(-1000.0)) shouldEqual Location(22.5)

        selectionLocationNearestTo(page, (23F + 57F) / 2 - 0.01F, 10F, Location(1000.0)) shouldEqual Location(22.5)
        selectionLocationNearestTo(page, (23F + 57F) / 2 - 0.01F, 10F, Location(22.6)) shouldEqual Location(35.0)
        selectionLocationNearestTo(page, (23F + 57F) / 2, 10F, Location(1000.0)) shouldEqual Location(30.0)
        selectionLocationNearestTo(page, (23F + 58F) / 2 - 0.01F, 10F, Location(-1000.0)) shouldEqual Location(22.5)
        selectionLocationNearestTo(page, (23F + 58F) / 2, 10F, Location(-1000.0)) shouldEqual Location(35.0)
        selectionLocationNearestTo(page, (63F + 66F) / 2, 10F, Location(-1000.0)) shouldEqual Location(45.0)
        selectionLocationNearestTo(page, (63F + 66F) / 2 + 0.01F, 10F, Location(-1000.0)) shouldEqual Location(30.0)
        selectionLocationNearestTo(page, 84F, 10F, Location(-1000.0)) shouldEqual Location(50.0)


        // text1, y = 10, height = 8:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        // text3, y = 20, height = 8:
        // x            18    19    20    24    67
        // text         |  a  |  b  |  c  |  d  |
        // loc          50   57.5   65   72.5   80

        selectionLocationNearestTo(page, 18F, (18F + 28F) / 2 - 0.01F, Location(1000.0)) shouldEqual Location(7.5)
        selectionLocationNearestTo(page, 18F, (18F + 28F) / 2, Location(1000.0)) shouldEqual Location(50.0)

        // text3:
        // x            18    19    20    24    67
        // text         |  a  |  b  |  c  |  d  |
        // loc          50   57.5   65   72.5   80

        // text4:
        // x                                                                  71    72    73    77          120
        // text                                                               |  a  |  b  |  c  |  d         |
        // loc                                                                90    91    92    93           94

        selectionLocationNearestTo(page, (24F + 71F) / 2 - 0.01F, 100F, Location(1000.0)) shouldEqual Location(72.5)
        selectionLocationNearestTo(page, (24F + 71F) / 2, 100F, Location(1000.0)) shouldEqual Location(90.0)
        selectionLocationNearestTo(page, (24F + 71F) / 2, 100F, Location(83.0)) shouldEqual Location(72.5)
        selectionLocationNearestTo(page, (24F + 72F) / 2, 100F, Location(83.0)) shouldEqual Location(91.0)

        selectionLocationNearestTo(page, (67F + 72F) / 2 - 0.01F, 100F, Location(-1000.0)) shouldEqual Location(80.0)
        selectionLocationNearestTo(page, (67F + 72F) / 2, 100F, Location(-1000.0)) shouldEqual Location(91.0)
        selectionLocationNearestTo(page, (67F + 72F) / 2, 100F, Location(83.0)) shouldEqual Location(91.0)
        selectionLocationNearestTo(page, (24F + 72F) / 2 - 0.01F, 100F, Location(83.0)) shouldEqual Location(72.5)

        selectionLocationNearestTo(page, 1000F, 1000F, Location(1000.0)) shouldEqual Location(93.0)
        selectionLocationNearestTo(page, 1000F, 1000F, Location(-1000.0)) shouldEqual Location(94.0)
    }

    @Test
    fun selectionHandlePositionAtLocation() {
        // given
        val charOffsets = floatArrayOf(1F, 2F, 3F, 4F)

        val text1 = text(50F, 8F, charOffsets, range(0, 30))
        val text2 = text(20F, 8F, charOffsets, range(30, 50))
        val text3 = text(50F, 8F, charOffsets, range(50, 80))
        val text4 = text(50F, 8F, charOffsets, range(90, 95))
        val page = page(
                LayoutBox(100F, 100F, listOf(
                        childLine(
                                11F, 10F, 80F, 10F, range(0, 50),
                                LayoutChild(16F - 11F, 10F - 10F, text1),
                                LayoutChild(56F - 11F, 10F - 10F, text2)   // пересекает предыдущий на 10 px
                        ),
                        childLine(
                                12F, 20F, 80F, 10F, range(50, 100),
                                LayoutChild(17F - 12F, 20F - 20F, text3),
                                LayoutChild(70F - 12F, 20F - 20F, text4)
                        )
                ), range(0, 100))
        )

        // expect
        round(range(0, 30).percentOf(location(3)) * 4) shouldEqual 0L
        round(range(0, 30).percentOf(location(4)) * 4) shouldEqual 1L
        round(range(0, 30).percentOf(location(26)) * 4) shouldEqual 3L
        round(range(0, 30).percentOf(location(27)) * 4) shouldEqual 4L
        text3.charLocation(3) shouldEqual Location(72.5)
        text4.charLocation(1) shouldEqual Location(91.25)

        selectionHandlePositionAt(page, location(-100), true) shouldEqual PositionF(17F, 10F + 8F)
        selectionHandlePositionAt(page, location(-1), true) shouldEqual PositionF(17F, 10F + 8F)
        selectionHandlePositionAt(page, location(0), true) shouldEqual PositionF(17F, 10F + 8F)
        selectionHandlePositionAt(page, location(1), true) shouldEqual PositionF(17F, 10F + 8F)
        selectionHandlePositionAt(page, location(3), true) shouldEqual PositionF(17F, 10F + 8F)
        selectionHandlePositionAt(page, location(4), true) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(26), true) shouldEqual PositionF(16F + 4F, 10F + 8F)
        selectionHandlePositionAt(page, location(27), true) shouldEqual PositionF(56F + 1F, 10F + 8F)
        selectionHandlePositionAt(page, location(30), true) shouldEqual PositionF(56F + 1F, 10F + 8F)
        selectionHandlePositionAt(page, location(50), true) shouldEqual PositionF(17F + 1F, 20F + 8F)
        selectionHandlePositionAt(page, location(80), true) shouldEqual PositionF(17F + 4F, 20F + 8F)
        selectionHandlePositionAt(page, location(81), true) shouldEqual PositionF(17F + 4F, 20F + 8F)
        selectionHandlePositionAt(page, location(82), true) shouldEqual PositionF(70F + 1F, 20F + 8F)
        selectionHandlePositionAt(page, location(95), true) shouldEqual PositionF(70F + 4F, 20F + 8F)
        selectionHandlePositionAt(page, location(100), true) shouldEqual PositionF(70F + 4F, 20F + 8F)
        selectionHandlePositionAt(page, location(1000), true) shouldEqual PositionF(70F + 4F, 20F + 8F)

        selectionHandlePositionAt(page, location(-100), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(-1), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(0), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(1), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(3), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(4), false) shouldEqual PositionF(16F + 2F, 10F + 8F)
        selectionHandlePositionAt(page, location(26), false) shouldEqual PositionF(16F + 4F, 10F + 8F)
        selectionHandlePositionAt(page, location(27), false) shouldEqual PositionF(16F + 50F, 10F + 8F)
        selectionHandlePositionAt(page, location(30), false) shouldEqual PositionF(16F + 50F, 10F + 8F)
        selectionHandlePositionAt(page, location(50), false) shouldEqual PositionF(56F + 20F, 10F + 8F)
        selectionHandlePositionAt(page, location(80), false) shouldEqual PositionF(17F + 50F, 20F + 8F)
        selectionHandlePositionAt(page, location(85), false) shouldEqual PositionF(17F + 50F, 20F + 8F)
        selectionHandlePositionAt(page, location(86), false) shouldEqual PositionF(70F + 2F, 20F + 8F)
        selectionHandlePositionAt(page, location(95), false) shouldEqual PositionF(70F + 50F, 20F + 8F)
        selectionHandlePositionAt(page, location(100), false) shouldEqual PositionF(70F + 50F, 20F + 8F)
        selectionHandlePositionAt(page, location(1000), false) shouldEqual PositionF(70F + 50F, 20F + 8F)
    }

    @Test fun selectionCharAtPosition() {
        // given
        val charOffsets = floatArrayOf(1F, 2F, 3F, 4F)
        val range = range(0, 30)

        val text1 = text(50F, 8F, charOffsets, range)
        val text2 = text(20F, 8F, charOffsets, range)
        val text3 = text(50F, 8F, charOffsets, range)
        val page = page(
                LayoutBox(100F, 100F, listOf(
                        childLine(
                                11F, 10F, 80F, 10F, range,
                                LayoutChild(16F - 11F, 10F - 10F, text1),
                                LayoutChild(56F - 11F, 10F - 10F, text2)   // пересекает предыдущий на 10 px
                        ),
                        childLine(
                                11F, 20F, 80F, 10F, range,
                                LayoutChild(20F - 11F, 20F - 20F, text3)
                        )
                ), range)
        )

        // expect
        selectionCharAt(page, -100F, -100F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 0F, 0F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 16F, 10F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 16 + 0.9F, 10F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 16 + 0.9F, -10F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 17F, 10F) shouldEqual LayoutChar(text1, 1)
        selectionCharAt(page, 56F - 0.1F, 10F) shouldEqual LayoutChar(text1, 3)
        selectionCharAt(page, 56F, 10F) shouldEqual LayoutChar(text2, 0)
        selectionCharAt(page, 560F, 10F) shouldEqual LayoutChar(text2, 3)
        selectionCharAt(page, -10F, 18F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, -10F, 19F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, -10F, 20F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 16F, 20F) shouldEqual LayoutChar(text1, 0)
        selectionCharAt(page, 17.9F, 20F) shouldEqual LayoutChar(text1, 1)
        selectionCharAt(page, 18F, 19F) shouldEqual LayoutChar(text1, 2)
        selectionCharAt(page, 18F, 20F) shouldEqual LayoutChar(text3, 0)
        selectionCharAt(page, 20F, 18.9F) shouldEqual LayoutChar(text1, 3)
        selectionCharAt(page, 20F, 19F) shouldEqual LayoutChar(text3, 0)
        selectionCharAt(page, -10F, 190F) shouldEqual LayoutChar(text3, 0)
        selectionCharAt(page, 120F, 190F) shouldEqual LayoutChar(text3, 3)


        // given 
        val emptyColumn = LayoutColumn(listOf(), 10F, range)
        val emptyPage = Page(emptyColumn, SizeF(10F, 10F), Page.Margins(0F, 0F, 0F, 0F))

        // expect
        selectionCharAt(emptyPage, 0F, 0F) shouldEqual null
        selectionCharAt(emptyPage, 5F, 5F) shouldEqual null
    }

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