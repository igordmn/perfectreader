package com.dmi.perfectreader.book.selection

import com.dmi.perfectreader.fontStyle
import com.dmi.perfectreader.location
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.*
import com.dmi.perfectreader.book.page.testPage
import com.dmi.perfectreader.range
import com.dmi.test.shouldBe
import org.junit.Test
import java.util.*

class PageSelectionsTest {
    @Test
    fun selectionCaretNearestToPosition() {
        // given
        val charOffsets = floatArrayOf(1F, 2F, 3F, 7F)

        val text1 = text(50F, 8F, charOffsets, range(0, 30))
        val text2 = text(20F, 8F, charOffsets, range(30, 50))
        val text3 = text(10F, 4F, charOffsets, range(50, 80))
        val text4 = text(10F, 8F, charOffsets, range(90, 94))
        val page = testPage(
                LayoutBox(100F, 100F, listOf(
                        childLine(
                                11F, 10F, 80F, 10F, range(0, 50),
                                LayoutChild(16F - 11F, 10F - 10F, text1),
                                LayoutChild(56F - 11F, 10F - 10F, text2)   // пересекает предыдущий на 10 px
                        ),
                        childLine(
                                12F, 20F, 80F, 10F, range(50, 100),
                                LayoutChild(17F - 12F, 22F - 20F, text3),
                                LayoutChild(30F - 12F, 20F - 20F, text4)
                        )
                ), range(0, 100))
        )

        // expect

        page.selectionCaretNearestTo(1000F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretNearestTo(1000F, 1000F, Location(-1000.0)) shouldBe LayoutCaret(text4, 4)
        page.selectionCaretNearestTo(-1000F, -1000F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(-1000F, -1000F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)


        // text1:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        page.selectionCaretNearestTo(-10F, -10F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(-10F, -10F, Location(7.5)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(-10F, -10F, Location(3.75)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(-10F, -10F, Location(3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(-10F, -10F, Location(0.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(-10F, -10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)

        page.selectionCaretNearestTo(17F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, 10F, Location(7.5)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, 10F, Location(3.75)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, 10F, Location(3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17F, 10F, Location(0.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)

        page.selectionCaretNearestTo(17F, -10F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, -10F, Location(7.5)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, -10F, Location(3.75)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17F, -10F, Location(3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17F, -10F, Location(0.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17F, -10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)

        page.selectionCaretNearestTo(17.49F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17.49F, 10F, Location(7.5)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17.49F, 10F, Location(3.75)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17.49F, 10F, Location(3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.49F, 10F, Location(0.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.49F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)

        page.selectionCaretNearestTo(17.5F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.5F, 10F, Location(15.0)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.5F, 10F, Location(7.5 + 3.75)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.5F, 10F, Location(7.5 + 3.74)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17.5F, 10F, Location(3.75)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(17.5F, 10F, Location(3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(17.5F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 1)

        page.selectionCaretNearestTo(18.5F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(18.5F, 10F, Location(22.5)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(18.5F, 10F, Location(15.0 + 3.75)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(18.5F, 10F, Location(15.0 + 3.74)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(18.5F, 10F, Location(7.5 + 3.75)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretNearestTo(18.5F, 10F, Location(7.5 + 3.74)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(18.5F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 2)


        // text1:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        // text2:
        // x                                57    58    59    63    83
        // text                             |  a  |  b  |  c  |  d  |
        // loc                              30    35    40    45    50

        page.selectionCaretNearestTo(26F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretNearestTo(26F, 10F, Location(30.0 - 3.75)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretNearestTo(26F, 10F, Location(30.0 - 3.76)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(26F, 10F, Location(22.5 - 3.75)) shouldBe LayoutCaret(text1, 2)
        page.selectionCaretNearestTo(26F, 10F, Location(22.5 - 3.76)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretNearestTo(26F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 3)

        page.selectionCaretNearestTo((23F + 57F) / 2 - 0.01F, 10F, Location(1000.0)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretNearestTo((23F + 57F) / 2 - 0.01F, 10F, Location(22.6)) shouldBe LayoutCaret(text2, 1)
        page.selectionCaretNearestTo((23F + 57F) / 2, 10F, Location(1000.0)) shouldBe LayoutCaret(text2, 0)
        page.selectionCaretNearestTo((23F + 58F) / 2 - 0.01F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretNearestTo((23F + 58F) / 2, 10F, Location(-1000.0)) shouldBe LayoutCaret(text2, 1)
        page.selectionCaretNearestTo((63F + 66F) / 2, 10F, Location(-1000.0)) shouldBe LayoutCaret(text2, 3)
        page.selectionCaretNearestTo((63F + 66F) / 2 + 0.01F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text1, 4)
        page.selectionCaretNearestTo(84F, 10F, Location(-1000.0)) shouldBe LayoutCaret(text2, 4)


        // text1, y = 10, height = 8:
        // x      17    18    19    23                           66
        // text   |  a  |  b  |  c  |  d                         |
        // loc    0    7.5    15   22.5                          30

        // text3, y = 22, height = 4:
        // x            18    19    20    24    27
        // text         |  a  |  b  |  c  |  d  |
        // loc          50   57.5   65   72.5   80

        // text4, y = 20, height = 8:
        // x                                                31    32    33    37    40
        // text                                             |  a  |  b  |  c  |  d  |
        // loc                                              90    91    92    93    94

        val text1caretBottom = 10F + 8
        val text3caretHalf = 22F + 4 / 2
        val text3caretBottom = 22F + 4

        page.selectionCaretNearestTo(-100F, (text1caretBottom + text3caretHalf) / 2 - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretNearestTo(0F, (text1caretBottom + text3caretHalf) / 2 - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text1, 0)

        page.selectionCaretNearestTo(-100F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretNearestTo(0F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 - 0.01F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretNearestTo((24F + 31F) / 2 + 0.01F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(66F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretNearestTo(1000F, (text1caretBottom + text3caretHalf) / 2 + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)

        page.selectionCaretNearestTo(-100F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretNearestTo(0F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 - 0.01F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretNearestTo((24F + 31F) / 2 + 0.01F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(1000F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)

        page.selectionCaretNearestTo(-100F, text3caretBottom + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(0F, text3caretBottom + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 - 0.01F, text3caretBottom + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 + 0.01F, text3caretBottom + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(1000F, text3caretBottom + 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)

        page.selectionCaretNearestTo(-100F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(0F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 - 0.01F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2 + 0.01F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo(1000F, 1000F, Location(1000.0)) shouldBe LayoutCaret(text4, 3)


        // text3, y = 22, height = 4:
        // x            18    19    20    24    27
        // text         |  a  |  b  |  c  |  d  |
        // loc          50   57.5   65   72.5   80

        // text4, y = 20, height = 8:
        // x                                                31    32    33    37    40
        // text                                             |  a  |  b  |  c  |  d  |
        // loc                                              90    91    92    93    94

        page.selectionCaretNearestTo((24F + 31F) / 2 - 0.01F, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretNearestTo((24F + 31F) / 2, text3caretBottom - 0.01F, Location(1000.0)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretNearestTo((24F + 31F) / 2, text3caretBottom - 0.01F, Location(83.0)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretNearestTo((24F + 32F) / 2, text3caretBottom - 0.01F, Location(83.0)) shouldBe LayoutCaret(text4, 1)

        page.selectionCaretNearestTo((27F + 32F) / 2 - 0.01F, text3caretBottom - 0.01F, Location(-1000.0)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretNearestTo((27F + 32F) / 2, text3caretBottom - 0.01F, Location(-1000.0)) shouldBe LayoutCaret(text4, 1)
        page.selectionCaretNearestTo((27F + 32F) / 2, text3caretBottom - 0.01F, Location(83.0)) shouldBe LayoutCaret(text4, 1)
        page.selectionCaretNearestTo((24F + 32F) / 2 - 0.01F, text3caretBottom - 0.01F, Location(83.0)) shouldBe LayoutCaret(text3, 3)
    }

    @Test
    fun selectionCaretAtLocation() {
        // given
        val charOffsets = floatArrayOf(0F, 0F, 0F, 0F)

        val text1 = text(0F, 0F, charOffsets, range(0, 40))
        val text2 = text(0F, 0F, charOffsets, range(40, 40))
        val text3 = text(0F, 0F, charOffsets, range(40, 80))
        val text4 = text(0F, 0F, charOffsets, range(200, 240))
        val page = testPage(
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
        page.selectionCaretAtLeft(location(-100)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtLeft(location(-1)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtLeft(location(0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtLeft(location(4)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtLeft(location(5)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretAtLeft(location(10)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretAtLeft(location(34)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretAtLeft(location(35)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretAtLeft(location(40)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretAtLeft(location(44)) shouldBe LayoutCaret(text3, 0)
        page.selectionCaretAtLeft(location(45)) shouldBe LayoutCaret(text3, 1)
        page.selectionCaretAtLeft(location(74)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretAtLeft(location(75)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretAtLeft(location(80)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretAtLeft(location(90)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretAtLeft(location(200)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretAtLeft(location(204)) shouldBe LayoutCaret(text4, 0)
        page.selectionCaretAtLeft(location(205)) shouldBe LayoutCaret(text4, 1)
        page.selectionCaretAtLeft(location(234)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretAtLeft(location(235)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretAtLeft(location(240)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretAtLeft(location(2340)) shouldBe LayoutCaret(text4, 3)

        page.selectionCaretAtRight(location(2340)) shouldBe LayoutCaret(text4, 4)
        page.selectionCaretAtRight(location(240)) shouldBe LayoutCaret(text4, 4)
        page.selectionCaretAtRight(location(235)) shouldBe LayoutCaret(text4, 4)
        page.selectionCaretAtRight(location(234)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretAtRight(location(230)) shouldBe LayoutCaret(text4, 3)
        page.selectionCaretAtRight(location(205)) shouldBe LayoutCaret(text4, 1)
        page.selectionCaretAtRight(location(204)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretAtRight(location(200)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretAtRight(location(90)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretAtRight(location(80)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretAtRight(location(75)) shouldBe LayoutCaret(text3, 4)
        page.selectionCaretAtRight(location(74)) shouldBe LayoutCaret(text3, 3)
        page.selectionCaretAtRight(location(45)) shouldBe LayoutCaret(text3, 1)
        page.selectionCaretAtRight(location(44)) shouldBe LayoutCaret(text1, 4)
        page.selectionCaretAtRight(location(40)) shouldBe LayoutCaret(text1, 4)
        page.selectionCaretAtRight(location(35)) shouldBe LayoutCaret(text1, 4)
        page.selectionCaretAtRight(location(34)) shouldBe LayoutCaret(text1, 3)
        page.selectionCaretAtRight(location(10)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretAtRight(location(5)) shouldBe LayoutCaret(text1, 1)
        page.selectionCaretAtRight(location(4)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtRight(location(0)) shouldBe LayoutCaret(text1, 0)
        page.selectionCaretAtRight(location(-100)) shouldBe LayoutCaret(text1, 0)
    }

    @Test
    fun clickableObjectAtPosition() {
        // given
        val range = range(0, 30)

        val obj1 = clickable(50F, 8F, range)
        val obj2 = clickable(20F, 8F, range)
        val obj3 = clickable(50F, 8F, range)
        val page = testPage(
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
        page.clickableObjectAt(0F, 0F, 0F) shouldBe null
        page.clickableObjectAt(11F, 10F, 0F) shouldBe null
        page.clickableObjectAt(16F, 10F, 0F) shouldBe obj1
        page.clickableObjectAt(56F, 10F, 0F) shouldBe obj2
        page.clickableObjectAt(56F + 20F, 10F, 0F) shouldBe obj2
        page.clickableObjectAt(56F + 20.1F, 10F, 0F) shouldBe null
        page.clickableObjectAt(17F - 1F, 20F, 0F) shouldBe null
        page.clickableObjectAt(17F, 20F, 0F) shouldBe obj3
        page.clickableObjectAt(17F + 50F, 20F, 0F) shouldBe obj3
        page.clickableObjectAt(17F + 51F, 20F, 0F) shouldBe null
        page.clickableObjectAt(17F + 510F, 20F, 0F) shouldBe null
        page.clickableObjectAt(17F, 20F + 8F, 0F) shouldBe obj3
        page.clickableObjectAt(17F, 20F + 8.1F, 0F) shouldBe null

        val squareOfTwo = (Math.sqrt(2.0) + 0.01).toFloat()
        page.clickableObjectAt(16F - 1F, 10F - 1F, squareOfTwo - 0.1F) shouldBe null
        page.clickableObjectAt(16F - 1F, 10F - 1F, squareOfTwo) shouldBe obj1
        page.clickableObjectAt(56F + 21F, 10F, 1F) shouldBe obj2
        page.clickableObjectAt(56F + 21.1F, 10F, 1F) shouldBe null
        page.clickableObjectAt(17F - 1.1F, 20F, 1F) shouldBe null
        page.clickableObjectAt(17F - 1F, 20F, 1F) shouldBe obj3
        page.clickableObjectAt(17F + 51F, 20F, 1F) shouldBe obj3
        page.clickableObjectAt(17F + 51.1F, 20F, 1F) shouldBe null
        page.clickableObjectAt(17F, 20F + 9F, 1F) shouldBe obj3
        page.clickableObjectAt(17F, 20F + 9.1F, 1F) shouldBe null
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
        return LayoutChild(x, y, LayoutLine(width, height, 0F, children, range))
    }

    fun clickable(width: Float, height: Float, range: LocationRange) = object : LayoutObject(width, height, listOf(), range) {
        override fun isClickable() = true
    }

    fun text(width: Float, height: Float, charOffsets: FloatArray, range: LocationRange) = LayoutText(
            width, height, textString(charOffsets.size), Locale.US, 4F, charOffsets, fontStyle(), range
    )

    fun textString(length: Int) = (1..length).map { 'a' }.joinToString("")
}