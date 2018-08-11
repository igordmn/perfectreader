package com.dmi.util.input

import com.dmi.util.collection.sumByFloat
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.distance
import com.dmi.util.graphic.sqrDistance

class TouchArea(val x: Float, val y: Float, val radius: Float) {
    operator fun times(multiplier: Float) = TouchArea(x * multiplier, y * multiplier, radius * multiplier)
    operator fun div(divider: Float) = TouchArea(x / divider, y / divider, radius / divider)
}

class TouchState(val fingers: Array<out TouchArea>) {
    val fingerCount: Int get() = fingers.size
}

enum class TouchAction { DOWN, UP, MOVE }

class TouchEvent(val action: TouchAction, val state: TouchState, val timeMillis: Long)

fun touchCenterArea(state: TouchState): TouchArea {
    require(state.fingerCount >= 1)
    val center = touchCenter(state)
    val radius = state.fingers.sumByFloat { it.radius } / state.fingerCount
    return TouchArea(center.x, center.y, radius)
}

fun touchCenter(state: TouchState): PositionF {
    require(state.fingerCount >= 1)
    var sumX = 0F
    var sumY = 0F
    for (i in 0..state.fingerCount - 1) {
        sumX += state.fingers[i].x
        sumY += state.fingers[i].y
    }
    return PositionF(sumX / state.fingerCount, sumY / state.fingerCount)
}

fun touchRadius(state: TouchState): Float {
    require(state.fingerCount >= 1)
    val center = touchCenter(state)
    return touchAverageDistance(center, state)
}

fun touchAverageDistance(position: PositionF, state: TouchState): Float {
    require(state.fingerCount >= 1)
    var distanceSum = 0F
    for (i in 0..state.fingerCount - 1) {
        distanceSum += distance(state.fingers[i].x, state.fingers[i].y, position.x, position.y)
    }
    return distanceSum / state.fingerCount
}

fun allFingersIsShifted(oldState: TouchState, newState: TouchState, minOffset: Float): Boolean {
    require(oldState.fingerCount == newState.fingerCount)
    for (i in 0..newState.fingerCount - 1) {
        val oldArea = oldState.fingers[i]
        val newArea = newState.fingers[i]
        if (sqrDistance(oldArea.x, oldArea.y, newArea.x, newArea.y) <= minOffset * minOffset) {
            return false
        }
    }
    return true
}

fun someFingersIsShifted(oldState: TouchState, newState: TouchState, minOffset: Float): Boolean {
    require(oldState.fingerCount == newState.fingerCount)
    for (i in 0..newState.fingerCount - 1) {
        val oldArea = oldState.fingers[i]
        val newArea = newState.fingers[i]
        if (sqrDistance(oldArea.x, oldArea.y, newArea.x, newArea.y) > minOffset * minOffset) {
            return true
        }
    }
    return false
}