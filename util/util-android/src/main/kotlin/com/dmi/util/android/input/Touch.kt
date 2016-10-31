package com.dmi.util.android.input

import android.view.MotionEvent
import com.dmi.util.input.TouchAction
import com.dmi.util.input.TouchArea
import com.dmi.util.input.TouchEvent
import com.dmi.util.input.TouchState

fun MotionEvent.performTouchEvents(perform: (TouchEvent) -> Unit) {
    when (actionMasked) {
        MotionEvent.ACTION_DOWN -> performCurrentTouchEvent(perform, TouchAction.DOWN)
        MotionEvent.ACTION_POINTER_DOWN -> performCurrentTouchEvent(perform, TouchAction.DOWN)
        MotionEvent.ACTION_POINTER_UP -> performCurrentTouchEvent(perform, TouchAction.UP)
        MotionEvent.ACTION_UP -> performCurrentTouchEvent(perform, TouchAction.UP)
        MotionEvent.ACTION_MOVE -> performMoveTouchEvents(perform)
    }
}

private fun MotionEvent.performMoveTouchEvents(perform: (TouchEvent) -> Unit) {
    performHistoricalTouchEvents(perform, TouchAction.MOVE)
    performCurrentTouchEvent(perform, TouchAction.MOVE)
}

private fun MotionEvent.performHistoricalTouchEvents(perform: (TouchEvent) -> Unit, action: TouchAction) {
    for (historyIndex in 0..historySize - 1) {
        val fingers = newTouchAreas { fingerIndex ->
            val x = getHistoricalX(fingerIndex, historyIndex)
            val y = getHistoricalY(fingerIndex, historyIndex)
            val touchMajor = getHistoricalTouchMajor(fingerIndex, historyIndex)
            TouchArea(x, y, touchMajor)
        }
        val eventTime = getHistoricalEventTime(historyIndex)
        val touchEvent = TouchEvent(action, TouchState(fingers), eventTime)
        perform(touchEvent)
    }
}

private fun MotionEvent.performCurrentTouchEvent(perform: (TouchEvent) -> Unit, action: TouchAction) {
    val fingers = newTouchAreas { fingerIndex ->
        val x = getX(fingerIndex)
        val y = getY(fingerIndex)
        val touchMajor = getTouchMajor(fingerIndex)
        TouchArea(x, y, touchMajor)
    }
    val touchEvent = TouchEvent(action, TouchState(fingers), eventTime)
    perform(touchEvent)
}

private inline fun MotionEvent.newTouchAreas(createFor: (index: Int) -> TouchArea): Array<TouchArea> {
    val fingerCount: Int
    val ignoredIndex: Int

    val isUp = actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP
    if (isUp) {
        fingerCount = pointerCount - 1
        ignoredIndex = actionIndex
    } else {
        fingerCount = pointerCount
        ignoredIndex = -1
    }

    var originalIndex = 0
    return Array(fingerCount) {
        if (originalIndex == ignoredIndex)
            originalIndex++
        val area = createFor(originalIndex)
        originalIndex++
        area
    }
}