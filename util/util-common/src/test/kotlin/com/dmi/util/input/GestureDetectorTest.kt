package com.dmi.util.input

import com.dmi.test.shouldEqual
import com.dmi.util.graphic.PositionF
import com.dmi.util.initTestPlatform
import com.dmi.util.input.GestureDetector.*
import com.dmi.util.lang.returnUnit
import com.dmi.util.mainScheduler
import org.junit.Test
import java.lang.Math.round
import java.util.*

private val MAX_TAP_OFFSET = 8F
private val DOUBLE_TAP_TIMEOUT_MILLIS = 20L
private val LONG_TAP_TIMEOUT_MILLIS = 20L

class GestureDetectorTest {
    init {
        initTestPlatform()
    }


    @Test
    fun `single tap without double tap enabled`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.SINGLE, area(0, 0, 1))
        sleep(100) leadsTo nothing
    }

    @Test
    fun `single tap with double tap enabled`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo nothing
        sleep(1) leadsTo nothing
        sleep(40) leadsTo OnSingleTap(FingerCount.SINGLE, area(0, 0, 1))
        sleep(100) leadsTo nothing
    }

    @Test
    fun `single tap with offset`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchMove(area(0, 7, 2)) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.SINGLE, area(0, 0, 1))
    }


    @Test
    fun `double tap`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(0, 0, 1)) leadsTo nothing

        sleep(1) leadsTo nothing
        touchUp() leadsTo nothing

        sleep(1) leadsTo nothing
        touchDown(area(1000, 1000, 1)) leadsTo OnDoubleTapStart(FingerCount.SINGLE, area(1000, 1000, 1))

        sleep(40) leadsTo nothing
        touchUp() leadsTo OnDoubleTapEnd

        sleep(100) leadsTo nothing
    }

    @Test
    fun `double tap ignore offsets and additional fingers`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo nothing
        touchDown(area(1000, 1000, 1)) leadsTo OnDoubleTapStart(FingerCount.SINGLE, area(1000, 1000, 1))

        touchMove(area(1000, 1000, 2)) leadsTo nothing
        touchMove(area(1000, 1000, 2), area(10, 10, 2)) leadsTo nothing
        touchMove(area(1000, 1000, 2)) leadsTo nothing
        touchMove(area(100, 100, 2)) leadsTo nothing

        touchUp() leadsTo OnDoubleTapEnd
    }


    @Test
    fun `long tap`() = testDetector {
        touchDown(area(0, 0, 1)) leadsTo nothing
        sleep(1) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.SINGLE, area(0, 0, 1))
        sleep(40) leadsTo nothing
        touchUp() leadsTo OnLongTapEnd
        sleep(100) leadsTo nothing
    }

    @Test
    fun `long tap with offset`() = testDetector {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchMove(area(0, 7, 2)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.SINGLE, area(0, 0, 1))
        touchUp() leadsTo OnLongTapEnd
    }

    @Test
    fun `long tap ignore offsets and additional fingers`() = testDetector {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchMove(area(0, 7, 2)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.SINGLE, area(0, 0, 1))

        touchMove(area(1000, 1000, 2)) leadsTo nothing
        touchDown(area(1000, 1000, 2), area(10, 10, 2)) leadsTo nothing
        touchUp(area(1000, 1000, 2)) leadsTo nothing
        touchMove(area(100, 100, 2)) leadsTo nothing

        touchUp() leadsTo OnLongTapEnd
    }


    @Test
    fun `two fingers single tap without double tap enabled`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(2, 2, 2)) leadsTo nothing
        sleep(1) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.MULTIPLE, area(6, 5, 3))
        sleep(100) leadsTo nothing
    }

    @Test
    fun `two fingers single tap with double tap enabled`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(2, 2, 2)) leadsTo nothing
        sleep(1) leadsTo nothing
        touchUp() leadsTo nothing
        sleep(1) leadsTo nothing
        sleep(40) leadsTo OnSingleTap(FingerCount.MULTIPLE, area(6, 5, 3))
        sleep(100) leadsTo nothing
    }

    @Test
    fun `two fingers single tap ignore offsets`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchMove(area(3, 3, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(10, 8, 4)) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.MULTIPLE, area(6, 5, 3))
        sleep(100) leadsTo nothing
    }

    @Test
    fun `multiple fingers single tap`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchMove(area(3, 3, 2), area(10, 8, 4)) leadsTo nothing
        touchDown(area(3, 3, 2), area(10, 8, 4), area(2, 1, 6)) leadsTo nothing
        touchUp(area(3, 3, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(10, 8, 4)) leadsTo nothing
        touchDown(area(10, 8, 4), area(10, 8, 4)) leadsTo nothing
        touchUp(area(10, 8, 4)) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.MULTIPLE, area(5, 4, 4))
        sleep(100) leadsTo nothing
    }


    @Test
    fun `two fingers double tap`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing

        touchUp(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(20, 20, 2)) leadsTo nothing
        touchUp(area(20, 20, 2)) leadsTo nothing
        touchMove() leadsTo nothing

        touchMove(area(100, 100, 4)) leadsTo nothing
        touchDown(area(100, 100, 4), area(200, 200, 2)) leadsTo OnDoubleTapStart(FingerCount.MULTIPLE, area(150, 150, 3))

        touchMove(area(400, 400, 4), area(200, 200, 2)) leadsTo nothing
        sleep(40) leadsTo nothing
        touchUp(area(400, 400, 4)) leadsTo nothing
        touchUp() leadsTo OnDoubleTapEnd

        sleep(100) leadsTo nothing
    }

    @Test
    fun `multiple fingers double tap`() = testDetector(doubleTapEnabled = true) {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4), area(12, 8, 4)) leadsTo nothing

        touchUp(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4), area(100, 800, 4)) leadsTo nothing
        touchMove(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchUp(area(100, 80, 4)) leadsTo nothing
        touchUp() leadsTo nothing

        touchDown(area(100, 100, 4)) leadsTo nothing
        touchMove(area(300, 300, 4)) leadsTo nothing
        touchUp() leadsTo nothing
        touchMove(area(100, 100, 4)) leadsTo nothing
        touchDown(area(100, 100, 4), area(200, 200, 2)) leadsTo nothing
        touchUp(area(100, 100, 4)) leadsTo nothing
        touchDown(area(100, 100, 4), area(200, 200, 2)) leadsTo nothing
        touchDown(area(100, 100, 2), area(200, 200, 2), area(300, 300, 2)) leadsTo OnDoubleTapStart(FingerCount.MULTIPLE, area(200, 200, 2))

        sleep(40) leadsTo nothing
        touchMove(area(100, 100, 2), area(200, 200, 2), area(500, 500, 2)) leadsTo nothing
        touchUp(area(100, 100, 2), area(200, 200, 2)) leadsTo nothing
        touchMove(area(100, 100, 2), area(300, 300, 2)) leadsTo nothing
        touchUp(area(100, 100, 2)) leadsTo nothing
        touchUp() leadsTo OnDoubleTapEnd

        sleep(100) leadsTo nothing
    }


    @Test
    fun `two fingers long tap`() = testDetector {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.MULTIPLE, area(6, 5, 3))
        touchUp(area(2, 2, 2)) leadsTo nothing
        sleep(40) leadsTo nothing
        touchUp() leadsTo OnLongTapEnd
    }

    @Test
    fun `two fingers long tap with offset`() = testDetector {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchMove(area(9, 2, 2), area(17, 8, 4)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.MULTIPLE, area(6, 5, 3))
        touchUp(area(2, 2, 2)) leadsTo nothing
        touchUp() leadsTo OnLongTapEnd
    }

    @Test
    fun `two fingers no long tap with one finger offset`() = testDetector {
        touchDown(area(2, 2, 2)) leadsTo nothing
        touchDown(area(2, 2, 2), area(10, 8, 4)) leadsTo nothing
        touchMove(area(2, 2, 2), area(19, 8, 4)) leadsTo nothing
        sleep(40) leadsTo nothing
        touchUp(area(2, 2, 2)) leadsTo nothing
        touchUp() leadsTo nothing
        sleep(40) leadsTo nothing
    }

    @Test
    fun `multiple fingers long tap`() = testDetector {
        touchDown(area(0, 0, 2)) leadsTo nothing
        touchDown(area(0, 0, 2), area(10, 10, 4)) leadsTo nothing
        touchDown(area(0, 0, 2), area(10, 10, 2), area(20, 20, 2)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.MULTIPLE, area(10, 10, 2))
        touchUp(area(0, 0, 2), area(10, 10, 2)) leadsTo nothing
        touchUp(area(0, 0, 2)) leadsTo nothing
        touchUp() leadsTo OnLongTapEnd
    }

    @Test
    fun `multiple fingers long tap with one finger left`() = testDetector {
        touchDown(area(0, 0, 2)) leadsTo nothing
        sleep(8) leadsTo nothing
        touchDown(area(0, 0, 2), area(10, 10, 4)) leadsTo nothing
        sleep(8) leadsTo nothing
        touchDown(area(0, 0, 2), area(10, 10, 2), area(20, 20, 2)) leadsTo nothing
        sleep(8) leadsTo nothing
        touchUp(area(0, 0, 2), area(10, 10, 4)) leadsTo nothing
        sleep(8) leadsTo nothing
        touchUp(area(0, 0, 2)) leadsTo nothing
        sleep(8) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.SINGLE, area(0, 0, 2))
        touchUp() leadsTo OnLongTapEnd
    }


    @Test
    fun `two fingers start pinch out`() = testDetector {
        touchDown(area(10, 0, 1)) leadsTo nothing
        touchDown(area(10, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchMove(area(10, 0, 1), area(50, 0, 1)) leadsTo nothing
        touchMove(area(0, 0, 1), area(50, 0, 1)) leadsTo listOf(OnPinchStart(PinchDirection.OUT), OnPinch(10))
        touchUp(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo OnPinchEnd
    }

    @Test
    fun `two fingers start pinch in`() = testDetector {
        touchDown(area(10, 0, 1)) leadsTo nothing
        touchDown(area(10, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchMove(area(10, 0, 1), area(30, 0, 1)) leadsTo nothing
        touchMove(area(20, 0, 1), area(30, 0, 1)) leadsTo listOf(OnPinchStart(PinchDirection.IN), OnPinch(-10))
        touchUp(area(20, 0, 1)) leadsTo nothing
        touchUp() leadsTo OnPinchEnd
    }

    @Test
    fun `multiple fingers start pinch out`() = testDetector {
        touchDown(area(10, 0, 1)) leadsTo nothing
        touchMove(area(15, 0, 1)) leadsTo nothing
        touchDown(area(15, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchMove(area(20, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchDown(area(20, 0, 1), area(40, 0, 1), area(60, 0, 1)) leadsTo nothing // radius = (40 - 20 + 40 - 40 + 60 - 40) / 3 = 13.33
        touchMove(area(13, 0, 1), area(40, 0, 1), area(67, 0, 1)) leadsTo nothing
        touchMove(area(11, 0, 1), area(40, 0, 1), area(69, 0, 1)) leadsTo nothing
        touchMove(area(11, 0, 1), area(49, 0, 1), area(69, 0, 1)) leadsTo listOf(OnPinchStart(PinchDirection.OUT), OnPinch(8)) // radius = (43 - 11 + 49 - 43 + 69 - 43) / 3 = 21
        touchUp(area(11, 0, 1), area(49, 0, 1)) leadsTo nothing
        touchUp(area(11, 0, 1)) leadsTo nothing
        touchUp() leadsTo OnPinchEnd
    }

    @Test
    fun `multiple fingers pinch`() = testDetector {
        touchDown(area(10, 0, 1)) leadsTo nothing
        touchDown(area(10, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchMove(area(20, 0, 1), area(30, 0, 1)) leadsTo listOf(OnPinchStart(PinchDirection.IN), OnPinch(-10))
        touchMove(area(20, 0, 1), area(30, 0, 1)) leadsTo nothing
        touchMove(area(20, 0, 1), area(32, 0, 1)) leadsTo OnPinch(1)
        touchMove(area(20, 0, 1), area(40, 0, 1)) leadsTo OnPinch(4)
        touchMove(area(25, 0, 1), area(35, 0, 1)) leadsTo OnPinch(-5)
        touchMove(area(35, 0, 1), area(25, 0, 1)) leadsTo nothing
        touchMove(area(24, 0, 1), area(36, 0, 1)) leadsTo OnPinch(1)
        touchMove(area(20, 0, 1), area(30, 0, 1)) leadsTo OnPinch(-1)
        touchMove(area(20, 0, 1), area(40, 0, 1)) leadsTo OnPinch(5)
        touchDown(area(20, 0, 1), area(40, 0, 1), area(60, 0, 1)) leadsTo nothing // radius = (40 - 20 + 40 - 40 + 60 - 40) / 3 = 13.33
        touchMove(area(0, 0, 1), area(40, 0, 1), area(80, 0, 1)) leadsTo OnPinch(13) // radius = (40 - 0 + 40 - 40 + 80 - 40) / 3 = 26.66
        touchUp(area(20, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchUp(area(20, 0, 1)) leadsTo nothing
        touchDown(area(20, 0, 1), area(30, 0, 1)) leadsTo nothing
        touchMove(area(20, 0, 1), area(40, 0, 1)) leadsTo OnPinch(5)
        touchUp(area(20, 0, 1)) leadsTo nothing
        touchUp() leadsTo OnPinchEnd
    }


    @Test
    fun `start scroll left`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(0, 9, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.LEFT, area(10, 10, 1)), OnScroll(-10, -1))
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `start scroll right`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(20, 9, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.RIGHT, area(10, 10, 1)), OnScroll(10, -1))
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `start scroll up`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(11, 0, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.UP, area(10, 10, 1)), OnScroll(1, -10))
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `start scroll down`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(11, 20, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.DOWN, area(10, 10, 1)), OnScroll(1, 10))
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `scroll delta`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(10, 20, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.DOWN, area(10, 10, 1)), OnScroll(0, 10))
        touchMove(0, area(11, 20, 1)) leadsTo OnScroll(1, 0)
        touchMove(0, area(10, 20, 1)) leadsTo OnScroll(-1, 0)
        touchMove(0, area(20, 30, 1)) leadsTo OnScroll(10, 10)
        touchMove(0, area(20, 30, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `scroll const velocity`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(1, area(20, 11, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.RIGHT, area(10, 10, 1)), OnScroll(10, 1))
        touchMove(2, area(30, 12, 1)) leadsTo OnScroll(10, 1)
        touchMove(3, area(40, 13, 1)) leadsTo OnScroll(10, 1)
        touchMove(4, area(50, 14, 1)) leadsTo OnScroll(10, 1)
        touchUp(5) leadsTo OnScrollEnd(10, 1)
    }

    @Test
    fun `scroll accelerating velocity`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(1, area(20, 11, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.RIGHT, area(10, 10, 1)), OnScroll(10, 1))
        touchMove(2, area(40, 13, 1)) leadsTo OnScroll(20, 2)
        touchMove(3, area(70, 16, 1)) leadsTo OnScroll(30, 3)
        touchMove(4, area(110, 20, 1)) leadsTo OnScroll(40, 4)
        touchUp(6) leadsTo OnScrollEnd(70, 7)
    }

    @Test
    fun `scroll small velocity at begin and big velocity later`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(14, 10, 1)) leadsTo nothing
        touchMove(0, area(18, 10, 1)) leadsTo nothing
        touchMove(0, area(22, 10, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.RIGHT, area(10, 10, 1)), OnScroll(12, 0))
        touchMove(2, area(32, 10, 1)) leadsTo OnScroll(10, 0)
        touchMove(3, area(42, 10, 1)) leadsTo OnScroll(10, 0)
        touchMove(4, area(52, 10, 1)) leadsTo OnScroll(10, 0)
        touchMove(5, area(62, 10, 1)) leadsTo OnScroll(10, 0)
        touchUp(6) leadsTo OnScrollEnd(14, 0)
    }


    @Test
    fun `two fingers start scroll left`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(0, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(0, 10, 1), area(20, 30, 3)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.LEFT, area(20, 20, 2)), OnScroll(-10, 0))
        touchUp(0, area(0, 10, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `two fingers start scroll right`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(10, 10, 1), area(40, 30, 3)) leadsTo nothing
        touchMove(0, area(20, 10, 1), area(40, 30, 3)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.RIGHT, area(20, 20, 2)), OnScroll(10, 0))
        touchUp(0, area(20, 10, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `two fingers start scroll up`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(10, 10, 1), area(30, 20, 3)) leadsTo nothing
        touchMove(0, area(10, 0, 1), area(30, 20, 3)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.UP, area(20, 20, 2)), OnScroll(0, -10))
        touchUp(0, area(10, 0, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `two fingers start scroll down`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(10, 10, 1), area(30, 40, 3)) leadsTo nothing
        touchMove(0, area(10, 20, 1), area(30, 40, 3)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.DOWN, area(20, 20, 2)), OnScroll(0, 10))
        touchUp(0, area(10, 20, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `two fingers start scroll left with one finger left`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(0, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchUp(0, area(0, 10, 1)) leadsTo nothing
        touchMove(0, area(-7, 10, 1)) leadsTo nothing
        touchMove(0, area(-10, 10, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.LEFT, area(0, 10, 1)), OnScroll(-10, 0))
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `multiple fingers scroll delta`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchDown(0, area(10, 10, 1), area(30, 30, 3)) leadsTo nothing
        touchMove(0, area(10, 10, 1), area(30, 40, 3)) leadsTo nothing
        touchMove(0, area(10, 20, 1), area(30, 40, 3)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.DOWN, area(20, 20, 2)), OnScroll(0, 10))
        touchMove(0, area(10, 10, 1), area(30, 30, 3)) leadsTo OnScroll(0, -10)
        touchMove(0, area(0, 10, 1), area(40, 30, 3)) leadsTo nothing
        touchUp(0, area(0, 10, 1)) leadsTo nothing
        touchUp(0) leadsTo OnScrollEnd(0, 0)
    }

    @Test
    fun `multiple fingers scroll const velocity`() = testDetector {
        touchDown(1, area(10, 10, 1)) leadsTo nothing
        touchDown(1, area(10, 10, 1), area(10, 100, 1)) leadsTo nothing
        touchMove(2, area(20, 10, 1), area(20, 100, 1)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.RIGHT, area(10, 55, 1)), OnScroll(10, 0))
        touchMove(3, area(30, 10, 1), area(30, 100, 1)) leadsTo OnScroll(10, 0)
        touchMove(4, area(40, 10, 1), area(40, 100, 1)) leadsTo OnScroll(10, 0)
        touchDown(4, area(40, 10, 1), area(40, 100, 1), area(40, 1000, 1)) leadsTo nothing
        touchMove(5, area(50, 10, 1), area(50, 100, 1), area(50, 1000, 1)) leadsTo OnScroll(10, 0)
        touchUp(5, area(50, 10, 1), area(50, 100, 1)) leadsTo nothing
        touchMove(6, area(60, 10, 1), area(60, 100, 1)) leadsTo OnScroll(10, 0)
        touchUp(6, area(60, 10, 1)) leadsTo nothing
        touchMove(7, area(70, 10, 1)) leadsTo OnScroll(10, 0)
        touchUp(7) leadsTo OnScrollEnd(10, 0)
    }

    @Test
    fun `multiple fingers scroll small velocity at begin and big later`() = testDetector {
        touchDown(1, area(10, 10, 1)) leadsTo nothing
        touchDown(1, area(10, 10, 1), area(10, 100, 1)) leadsTo nothing
        touchMove(2, area(14, 10, 1), area(14, 100, 1)) leadsTo nothing
        touchMove(3, area(18, 10, 1), area(18, 100, 1)) leadsTo nothing
        touchMove(4, area(22, 10, 1), area(22, 100, 1)) leadsTo listOf(OnScrollStart(FingerCount.MULTIPLE, Direction.RIGHT, area(10, 55, 1)), OnScroll(12, 0))
        touchDown(4, area(22, 10, 1), area(22, 100, 1), area(22, 1000, 1)) leadsTo nothing
        touchMove(5, area(26, 10, 1), area(26, 100, 1), area(26, 1000, 1)) leadsTo OnScroll(4, 0)
        touchUp(5, area(26, 10, 1), area(26, 100, 1)) leadsTo nothing
        touchMove(6, area(36, 10, 1), area(36, 100, 1)) leadsTo OnScroll(10, 0)
        touchUp(6, area(36, 10, 1)) leadsTo nothing
        touchMove(7, area(46, 10, 1)) leadsTo OnScroll(10, 0)
        touchUp(7) leadsTo OnScrollEnd(11, 0)
    }


    @Test
    fun `cancel long tap`() = testDetector {
        touchDown(area(0, 0, 1)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.SINGLE, area(0, 0, 1))
        cancel() leadsTo OnLongTapCancel
        cancel() leadsTo nothing
        touchUp() leadsTo nothing
    }

    @Test
    fun `cancel double tap`() = testDetector {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo nothing
        touchDown(area(1000, 1000, 1)) leadsTo OnDoubleTapStart(FingerCount.SINGLE, area(1000, 1000, 1))
        cancel() leadsTo OnDoubleTapCancel
        cancel() leadsTo nothing
        touchUp() leadsTo nothing
    }

    @Test
    fun `cancel two fingers long tap`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchDown(area(0, 0, 1), area(0, 0, 1)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.MULTIPLE, area(0, 0, 1))
        cancel() leadsTo OnLongTapCancel
        touchUp(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo nothing
        sleep(40) leadsTo nothing
    }

    @Test
    fun `single tap after cancel two fingers long tap`() = testDetector(doubleTapEnabled = false) {
        touchDown(area(0, 0, 1)) leadsTo nothing
        touchDown(area(0, 0, 1), area(0, 0, 1)) leadsTo nothing
        sleep(40) leadsTo OnLongTapStart(FingerCount.MULTIPLE, area(0, 0, 1))
        cancel() leadsTo OnLongTapCancel

        touchDown(area(10, 10, 1)) leadsTo nothing
        touchUp() leadsTo OnSingleTap(FingerCount.SINGLE, area(10, 10, 1))
    }

    @Test
    fun `cancel scroll`() = testDetector {
        touchDown(0, area(10, 10, 1)) leadsTo nothing
        touchMove(0, area(0, 9, 1)) leadsTo listOf(OnScrollStart(FingerCount.SINGLE, Direction.LEFT, area(10, 10, 1)), OnScroll(-10, -1))
        cancel() leadsTo OnScrollCancel
        touchUp() leadsTo nothing
    }

    @Test
    fun `cancel pinch`() = testDetector {
        touchDown(area(10, 0, 1)) leadsTo nothing
        touchDown(area(10, 0, 1), area(40, 0, 1)) leadsTo nothing
        touchMove(area(10, 0, 1), area(50, 0, 1)) leadsTo nothing
        touchMove(area(0, 0, 1), area(50, 0, 1)) leadsTo listOf(OnPinchStart(PinchDirection.OUT), OnPinch(10))
        cancel() leadsTo OnPinchCancel
        touchUp(area(0, 0, 1)) leadsTo nothing
        touchUp() leadsTo nothing
    }

    fun area(x: Int, y: Int, radius: Int) = TestArea(x, y, radius)
    data class TestArea(val x: Int, val y: Int, val radius: Int)

    fun TestArea.toTouchArea() = TouchArea(x.toFloat(), y.toFloat(), radius.toFloat())
    fun TouchArea.toTestArea() = area(round(x), round(y), round(radius))

    fun testDetector(doubleTapEnabled: Boolean = true, test: DetectorTester.() -> Unit) {
        val collector = ActionCollector()
        val detector = GestureDetector(
                mainScheduler,
                collector,
                doubleTapEnabled,
                MAX_TAP_OFFSET,
                LONG_TAP_TIMEOUT_MILLIS,
                DOUBLE_TAP_TIMEOUT_MILLIS
        )
        DetectorTester(collector, detector).test()
    }

    inner class DetectorTester(val collector: ActionCollector, val detector: GestureDetector) {
        fun touchEvent(action: TouchAction, vararg fingerAreas: TestArea): List<Action> =
                collector.newActionsAfter {
                    detector.onTouchEvent(action, System.nanoTime() / 1000000, *fingerAreas)
                }

        fun touchEvent(action: TouchAction, millis: Long, vararg fingerAreas: TestArea): List<Action> =
                collector.newActionsAfter {
                    detector.onTouchEvent(action, millis, *fingerAreas)
                }

        private fun GestureDetector.onTouchEvent(action: TouchAction, millis: Long, vararg fingerAreas: TestArea) =
                onTouchEvent(TouchEvent(
                        action,
                        TouchState(fingerAreas.map { it.toTouchArea() }.toTypedArray()),
                        millis
                ))

        fun touchMove(vararg fingerAreas: TestArea) = touchEvent(TouchAction.MOVE, *fingerAreas)
        fun touchDown(vararg fingerAreas: TestArea) = touchEvent(TouchAction.DOWN, *fingerAreas)
        fun touchUp(vararg fingerAreas: TestArea) = touchEvent(TouchAction.UP, *fingerAreas)

        fun touchMove(millis: Long, vararg fingerAreas: TestArea) = touchEvent(TouchAction.MOVE, millis, *fingerAreas)
        fun touchDown(millis: Long, vararg fingerAreas: TestArea) = touchEvent(TouchAction.DOWN, millis, *fingerAreas)
        fun touchUp(millis: Long, vararg fingerAreas: TestArea) = touchEvent(TouchAction.UP, millis, *fingerAreas)

        fun cancel(): List<Action> =
                collector.newActionsAfter { detector.cancel() }

        fun sleep(millis: Long): List<Action> =
                collector.newActionsAfter { Thread.sleep(millis) }

        infix fun List<Action>.leadsTo(action: Action) = this shouldEqual listOf(action)
        infix fun List<Action>.leadsTo(actions: List<Action>) = this shouldEqual actions

        val nothing: List<Action> = emptyList()
    }

    inner class ActionCollector : GestureDetector.Listener {
        private val actions = ArrayList<Action>()
        val newActions: List<Action>
            get() {
                val actions = ArrayList<Action>(actions)
                this.actions.clear()
                return actions
            }

        fun newActionsAfter(action: () -> Unit) = action().run { newActions }

        override fun onSingleTap(fingerCount: FingerCount, focusArea: TouchArea) =
                actions.add(OnSingleTap(fingerCount, focusArea.toTestArea())).returnUnit()

        override fun onLongTap(fingerCount: FingerCount, focusArea: TouchArea) =
                object : GestureDetector.OnTapListener {
                    init {
                        actions.add(OnLongTapStart(fingerCount, focusArea.toTestArea()))
                    }

                    override fun onEnd() = actions.add(OnLongTapEnd).returnUnit()
                    override fun onCancel() = actions.add(OnLongTapCancel).returnUnit()
                }

        override fun onDoubleTap(fingerCount: FingerCount, focusArea: TouchArea) =
                object : GestureDetector.OnTapListener {
                    init {
                        actions.add(OnDoubleTapStart(fingerCount, focusArea.toTestArea()))
                    }

                    override fun onEnd() = actions.add(OnDoubleTapEnd).returnUnit()
                    override fun onCancel() = actions.add(OnDoubleTapCancel).returnUnit()
                }

        override fun onScroll(fingerCount: FingerCount, direction: Direction, startArea: TouchArea) =
                object : GestureDetector.OnScrollListener {
                    init {
                        actions.add(OnScrollStart(fingerCount, direction, startArea.toTestArea()))
                    }

                    override fun onScroll(delta: PositionF) = actions.add(OnScroll(round(delta.x), round(delta.y))).returnUnit()
                    override fun onEnd(velocity: PositionF) = actions.add(OnScrollEnd(round(velocity.x / 1000), round(velocity.y / 1000))).returnUnit()
                    override fun onCancel() = actions.add(OnScrollCancel).returnUnit()
                }

        override fun onPinch(direction: PinchDirection) =
                object : GestureDetector.OnPinchListener {
                    init {
                        actions.add(OnPinchStart(direction))
                    }

                    override fun onPinch(radiusDelta: Float) = actions.add(OnPinch(round(radiusDelta))).returnUnit()
                    override fun onEnd() = actions.add(OnPinchEnd).returnUnit()
                    override fun onCancel() = actions.add(OnPinchCancel).returnUnit()
                }
    }

    interface Action

    data class OnSingleTap(val fingerCount: FingerCount, val focusArea: TestArea) : Action

    data class OnLongTapStart(val fingerCount: FingerCount, val focusArea: TestArea) : Action
    object OnLongTapEnd : Action
    object OnLongTapCancel : Action

    data class OnDoubleTapStart(val fingerCount: FingerCount, val focusArea: TestArea) : Action
    object OnDoubleTapEnd : Action
    object OnDoubleTapCancel : Action

    data class OnScrollStart(val fingerCount: FingerCount, val direction: Direction, val startArea: TestArea) : Action
    data class OnScroll(val deltaX: Int, val deltaY: Int) : Action
    data class OnScrollEnd(val velocityXMilliseconds: Int, val velocityYMilliseconds: Int) : Action
    object OnScrollCancel : Action

    data class OnPinchStart(val direction: PinchDirection) : Action
    data class OnPinch(val radiusDelta: Int) : Action
    object OnPinchEnd : Action
    object OnPinchCancel : Action
}