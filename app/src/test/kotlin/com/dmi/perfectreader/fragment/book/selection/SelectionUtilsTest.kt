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
import org.junit.Test
import java.util.*

class SelectionUtilsTest {
//    @Test
//    fun selectionCaretNearestToPosition() {
//        // given
//        val charOffsets = floatArrayOf(1F, 2F, 3F, 7F)
//
//        val text1 = text(50F, 8F, charOffsets, range(0, 30))
//        val text2 = text(20F, 8F, charOffsets, range(30, 50))
//        val text3 = text(50F, 8F, charOffsets, range(50, 80))
//        val text4 = text(50F, 8F, charOffsets, range(90, 94))
//        val page = page(
//                LayoutBox(100F, 100F, listOf(
//                        childLine(
//                                11F, 10F, 80F, 10F, range(0, 50),
//                                LayoutChild(16F - 11F, 10F - 10F, text1),
//                                LayoutChild(56F - 11F, 10F - 10F, text2)   // пересекает предыдущий на 10 px
//                        ),
//                        childLine(
//                                12F, 20F, 80F, 10F, range(50, 100),
//                                LayoutChild(17F - 12F, 20F - 20F, text3),
//                                LayoutChild(70F - 12F, 20F - 20F, text4)
//                        )
//                ), range(0, 100))
//        )
//
//        // expect
//
//        // text1:
//        // x      17    18    19    23                           66
//        // text   |  a  |  b  |  c  |  d                         |
//        // loc    0    7.5    15   22.5                          30
//
//        selectionLocationNearestTo(page, -10F, -10F, Location(1000.0)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, -10F, -10F, Location(7.5)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, -10F, -10F, Location(3.75)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, -10F, -10F, Location(3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, -10F, -10F, Location(0.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, -10F, -10F, Location(-1000.0)) shouldEqual Location(7.5)
//
//        selectionLocationNearestTo(page, 17F, 10F, Location(1000.0)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, 10F, Location(7.5)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, 10F, Location(3.75)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, 10F, Location(3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17F, 10F, Location(0.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17F, 10F, Location(-1000.0)) shouldEqual Location(7.5)
//
//        selectionLocationNearestTo(page, 17F, -10F, Location(1000.0)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, -10F, Location(7.5)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, -10F, Location(3.75)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17F, -10F, Location(3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17F, -10F, Location(0.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17F, -10F, Location(-1000.0)) shouldEqual Location(7.5)
//
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(1000.0)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(7.5)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(3.75)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(0.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.49F, 10F, Location(-1000.0)) shouldEqual Location(7.5)
//
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(1000.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(15.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(7.5 + 3.75)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(7.5 + 3.74)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(3.75)) shouldEqual Location(0.0)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 17.5F, 10F, Location(-1000.0)) shouldEqual Location(7.5)
//
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(1000.0)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(22.5)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(15.0 + 3.75)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(15.0 + 3.74)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(7.5 + 3.75)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(7.5 + 3.74)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 18.5F, 10F, Location(-1000.0)) shouldEqual Location(15.0)
//
//
//        // text1:
//        // x      17    18    19    23                           66
//        // text   |  a  |  b  |  c  |  d                         |
//        // loc    0    7.5    15   22.5                          30
//
//        // text2:
//        // x                                57    58    59    63    83
//        // text                             |  a  |  b  |  c  |  d  |
//        // loc                              30    35    40    45    50
//
//        selectionLocationNearestTo(page, 26F, 10F, Location(1000.0)) shouldEqual Location(22.5)
//        selectionLocationNearestTo(page, 26F, 10F, Location(30.0 - 3.75)) shouldEqual Location(22.5)
//        selectionLocationNearestTo(page, 26F, 10F, Location(30.0 - 3.76)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 26F, 10F, Location(22.5 - 3.75)) shouldEqual Location(15.0)
//        selectionLocationNearestTo(page, 26F, 10F, Location(22.5 - 3.76)) shouldEqual Location(22.5)
//        selectionLocationNearestTo(page, 26F, 10F, Location(-1000.0)) shouldEqual Location(22.5)
//
//        selectionLocationNearestTo(page, (23F + 57F) / 2 - 0.01F, 10F, Location(1000.0)) shouldEqual Location(22.5)
//        selectionLocationNearestTo(page, (23F + 57F) / 2 - 0.01F, 10F, Location(22.6)) shouldEqual Location(35.0)
//        selectionLocationNearestTo(page, (23F + 57F) / 2, 10F, Location(1000.0)) shouldEqual Location(30.0)
//        selectionLocationNearestTo(page, (23F + 58F) / 2 - 0.01F, 10F, Location(-1000.0)) shouldEqual Location(22.5)
//        selectionLocationNearestTo(page, (23F + 58F) / 2, 10F, Location(-1000.0)) shouldEqual Location(35.0)
//        selectionLocationNearestTo(page, (63F + 66F) / 2, 10F, Location(-1000.0)) shouldEqual Location(45.0)
//        selectionLocationNearestTo(page, (63F + 66F) / 2 + 0.01F, 10F, Location(-1000.0)) shouldEqual Location(30.0)
//        selectionLocationNearestTo(page, 84F, 10F, Location(-1000.0)) shouldEqual Location(50.0)
//
//
//        // text1, y = 10, height = 8:
//        // x      17    18    19    23                           66
//        // text   |  a  |  b  |  c  |  d                         |
//        // loc    0    7.5    15   22.5                          30
//
//        // text3, y = 20, height = 8:
//        // x            18    19    20    24    67
//        // text         |  a  |  b  |  c  |  d  |
//        // loc          50   57.5   65   72.5   80
//
//        selectionLocationNearestTo(page, 18F, (18F + 28F) / 2 - 0.01F, Location(1000.0)) shouldEqual Location(7.5)
//        selectionLocationNearestTo(page, 18F, (18F + 28F) / 2, Location(1000.0)) shouldEqual Location(50.0)
//
//        // text3:
//        // x            18    19    20    24    67
//        // text         |  a  |  b  |  c  |  d  |
//        // loc          50   57.5   65   72.5   80
//
//        // text4:
//        // x                                                                  71    72    73    77          120
//        // text                                                               |  a  |  b  |  c  |  d         |
//        // loc                                                                90    91    92    93           94
//
//        selectionLocationNearestTo(page, (24F + 71F) / 2 - 0.01F, 100F, Location(1000.0)) shouldEqual Location(72.5)
//        selectionLocationNearestTo(page, (24F + 71F) / 2, 100F, Location(1000.0)) shouldEqual Location(90.0)
//        selectionLocationNearestTo(page, (24F + 71F) / 2, 100F, Location(83.0)) shouldEqual Location(72.5)
//        selectionLocationNearestTo(page, (24F + 72F) / 2, 100F, Location(83.0)) shouldEqual Location(91.0)
//
//        selectionLocationNearestTo(page, (67F + 72F) / 2 - 0.01F, 100F, Location(-1000.0)) shouldEqual Location(80.0)
//        selectionLocationNearestTo(page, (67F + 72F) / 2, 100F, Location(-1000.0)) shouldEqual Location(91.0)
//        selectionLocationNearestTo(page, (67F + 72F) / 2, 100F, Location(83.0)) shouldEqual Location(91.0)
//        selectionLocationNearestTo(page, (24F + 72F) / 2 - 0.01F, 100F, Location(83.0)) shouldEqual Location(72.5)
//
//        selectionLocationNearestTo(page, 1000F, 1000F, Location(1000.0)) shouldEqual Location(93.0)
//        selectionLocationNearestTo(page, 1000F, 1000F, Location(-1000.0)) shouldEqual Location(94.0)
//    }

    @Test
    fun selectionCaretAtLocation() {
        // given
        val charOffsets = floatArrayOf(0F, 0F, 0F, 0F)

        val text1 = text(0F, 0F, charOffsets, range(0, 40))
        val text2 = text(0F, 0F, charOffsets, range(40, 40))
        val text3 = text(0F, 0F, charOffsets, range(40, 80))
        val text4 = text(0F, 0F, charOffsets, range(200, 240))
        val page = page(
                LayoutBox(0F, 0F, listOf(
                        childLine(
                                0F, 0F, 0F, 0F, range(0, 40),
                                LayoutChild(0F, 0F, text1),
                                LayoutChild(0F, 0F, text2)
                        ),
                        childLine(
                                0F, 0F, 0F, 0F, range(80, 300),
                                LayoutChild(0F, 0F, text3),
                                LayoutChild(0F, 0F, text4)
                        )
                ), range(0, 300))
        )

        // expect
        selectionCaretAtLeft(page, location(-100)) shouldEqual Caret(text1, 0)
        selectionCaretAtLeft(page, location(-1)) shouldEqual Caret(text1, 0)
        selectionCaretAtLeft(page, location(0)) shouldEqual Caret(text1, 0)
        selectionCaretAtLeft(page, location(4)) shouldEqual Caret(text1, 0)
        selectionCaretAtLeft(page, location(5)) shouldEqual Caret(text1, 1)
        selectionCaretAtLeft(page, location(10)) shouldEqual Caret(text1, 1)
        selectionCaretAtLeft(page, location(34)) shouldEqual Caret(text1, 3)
        selectionCaretAtLeft(page, location(35)) shouldEqual Caret(text3, 0)
        selectionCaretAtLeft(page, location(40)) shouldEqual Caret(text3, 0)
        selectionCaretAtLeft(page, location(44)) shouldEqual Caret(text3, 0)
        selectionCaretAtLeft(page, location(45)) shouldEqual Caret(text3, 1)
        selectionCaretAtLeft(page, location(74)) shouldEqual Caret(text3, 3)
        selectionCaretAtLeft(page, location(75)) shouldEqual Caret(text4, 0)
        selectionCaretAtLeft(page, location(80)) shouldEqual Caret(text4, 0)
        selectionCaretAtLeft(page, location(90)) shouldEqual Caret(text4, 0)
        selectionCaretAtLeft(page, location(200)) shouldEqual Caret(text4, 0)
        selectionCaretAtLeft(page, location(204)) shouldEqual Caret(text4, 0)
        selectionCaretAtLeft(page, location(205)) shouldEqual Caret(text4, 1)
        selectionCaretAtLeft(page, location(234)) shouldEqual Caret(text4, 3)
        selectionCaretAtLeft(page, location(235)) shouldEqual Caret(text4, 3)
        selectionCaretAtLeft(page, location(240)) shouldEqual Caret(text4, 3)
        selectionCaretAtLeft(page, location(2340)) shouldEqual Caret(text4, 3)
        
        selectionCaretAtRight(page, location(2340)) shouldEqual Caret(text4, 4)
        selectionCaretAtRight(page, location(240)) shouldEqual Caret(text4, 4)
        selectionCaretAtRight(page, location(235)) shouldEqual Caret(text4, 4)
        selectionCaretAtRight(page, location(234)) shouldEqual Caret(text4, 3)
        selectionCaretAtRight(page, location(230)) shouldEqual Caret(text4, 3)
        selectionCaretAtRight(page, location(205)) shouldEqual Caret(text4, 1)
        selectionCaretAtRight(page, location(204)) shouldEqual Caret(text3, 4)
        selectionCaretAtRight(page, location(200)) shouldEqual Caret(text3, 4)
        selectionCaretAtRight(page, location(90)) shouldEqual Caret(text3, 4)
        selectionCaretAtRight(page, location(80)) shouldEqual Caret(text3, 4)
        selectionCaretAtRight(page, location(75)) shouldEqual Caret(text3, 4)
        selectionCaretAtRight(page, location(74)) shouldEqual Caret(text3, 3)
        selectionCaretAtRight(page, location(45)) shouldEqual Caret(text3, 1)
        selectionCaretAtRight(page, location(44)) shouldEqual Caret(text1, 4)
        selectionCaretAtRight(page, location(40)) shouldEqual Caret(text1, 4)
        selectionCaretAtRight(page, location(35)) shouldEqual Caret(text1, 4)
        selectionCaretAtRight(page, location(34)) shouldEqual Caret(text1, 3)
        selectionCaretAtRight(page, location(10)) shouldEqual Caret(text1, 1)
        selectionCaretAtRight(page, location(5)) shouldEqual Caret(text1, 1)
        selectionCaretAtRight(page, location(4)) shouldEqual Caret(text1, 0)
        selectionCaretAtRight(page, location(0)) shouldEqual Caret(text1, 0)
        selectionCaretAtRight(page, location(-100)) shouldEqual Caret(text1, 0)
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