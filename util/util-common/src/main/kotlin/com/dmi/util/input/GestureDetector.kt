package com.dmi.util.input

import com.dmi.util.graphic.PositionF
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.abs
import kotlin.coroutines.CoroutineContext

// todo cancel stateJob on dispose
class GestureDetector(
        private val context: CoroutineContext,
        private val listener: Listener,
        private val doubleTapEnabled: Boolean,
        private val tapMaxOffset: Float,
        private val longTapTimeoutMillis: Long,
        private val doubleTapTimeoutMillis: Long
) {
    interface OnTapListener {
        fun onEnd()
        fun onCancel()
    }

    interface OnScrollListener {
        fun onScroll(delta: PositionF)
        fun onEnd(velocity: PositionF)
        fun onCancel()
    }

    interface OnPinchListener {
        fun onPinch(radiusDelta: Float)
        fun onEnd()
        fun onCancel()
    }

    interface Listener {
        fun onSingleTap(fingerCount: FingerCount, focusArea: TouchArea)
        fun onLongTap(fingerCount: FingerCount, focusArea: TouchArea): OnTapListener
        fun onDoubleTap(fingerCount: FingerCount, focusArea: TouchArea): OnTapListener
        fun onScroll(fingerCount: FingerCount, direction: Direction, startArea: TouchArea): OnScrollListener
        fun onPinch(direction: PinchDirection): OnPinchListener
    }

    private var state: State = State.None
        private set(value) {
            stateJob.cancel()
            stateJob = Job()
            field = value
        }
    private var stateJob = Job()
    private val velocityTracker = VelocityTracker()
    private var velocityTrackerFingerCount = 0

    fun onTouchEvent(event: TouchEvent) {
        val state = state
        val touchState = event.state
        val action = event.action
        val fingerCount = touchState.fingerCount

        @Suppress("IntroduceWhenSubject")
        when (state) {
            is State.None -> when {
                action == TouchAction.DOWN && fingerCount == 1 -> awaitLongTap(touchState, touchState)
            }
            is State.AwaitLongTap -> when {
                fingerCount == 0 && doubleTapEnabled -> awaitSecondTap(state.maxDownState)
                fingerCount == 0 && !doubleTapEnabled -> onSingleTapUp(state.maxDownState)
                fingerCount != state.lastDownState.fingerCount -> {
                    val maxDownState = if (state.maxDownState.fingerCount > fingerCount) state.maxDownState else touchState
                    awaitLongTap(touchState, maxDownState)
                }
                allFingersIsShifted(state.lastDownState, touchState, tapMaxOffset) -> startScrollOrPinch(state.lastDownState, touchState)
                someFingersIsShifted(state.lastDownState, touchState, tapMaxOffset) -> awaitScroll(state.lastDownState, state.lastDownState)
            }
            is State.AwaitScroll -> when {
                fingerCount == 0 -> this.state = State.None
                fingerCount != state.lastDownState.fingerCount -> {
                    val maxDownState = if (state.maxDownState.fingerCount > fingerCount) state.maxDownState else touchState
                    awaitScroll(touchState, maxDownState)
                }
                allFingersIsShifted(state.lastDownState, touchState, tapMaxOffset) -> startScrollOrPinch(state.lastDownState, touchState)
            }
            is State.AwaitSecondTap -> when {
                fingerCount == state.maxDownState.fingerCount -> onDoubleTapDown(touchState)
            }
            is State.Tap -> when {
                fingerCount == 0 -> endTapRepeat(state)
            }
            is State.Scroll -> when {
                fingerCount == 0 -> endScroll(state, velocityTracker.currentVelocity())
                fingerCount != state.lastState.fingerCount -> state.lastState = touchState
                else -> scroll(state, touchState)
            }
            is State.Pinch -> when {
                fingerCount == 0 -> endPinch(state)
                fingerCount != state.lastState.fingerCount -> state.lastState = touchState
                else -> pinch(state, touchState)
            }
        }

        if (touchState.fingerCount != velocityTrackerFingerCount) {
            velocityTracker.clear()
        }
        if (touchState.fingerCount > 0) {
            val center = touchCenter(touchState)
            val seconds = event.timeMillis / 1000.0

            velocityTracker.addMovement(center.x.toDouble(), center.y.toDouble(), seconds)
        }
        velocityTrackerFingerCount = event.state.fingerCount
    }

    private fun awaitLongTap(lastDownState: TouchState, maxDownState: TouchState) {
        this.state = State.AwaitLongTap(lastDownState, maxDownState)
        delay(longTapTimeoutMillis) {
            onLongTapDown(lastDownState)
        }
    }

    private fun awaitSecondTap(maxDownState: TouchState) {
        this.state = State.AwaitSecondTap(maxDownState)
        delay(doubleTapTimeoutMillis) {
            onSingleTapUp(maxDownState)
        }
    }

    private fun onLongTapDown(maxDownState: TouchState) {
        val fingerCount = recognizeFingerCount(maxDownState)
        val focusArea = touchCenterArea(maxDownState)
        val onTapRepeatListener = listener.onLongTap(fingerCount, focusArea)
        startTapRepeat(onTapRepeatListener)
    }

    private fun onDoubleTapDown(secondMaxDownState: TouchState) {
        val fingerCount = recognizeFingerCount(secondMaxDownState)
        val focusArea = touchCenterArea(secondMaxDownState)
        val onTapRepeatListener = listener.onDoubleTap(fingerCount, focusArea)
        startTapRepeat(onTapRepeatListener)
    }

    private fun onSingleTapUp(maxDownState: TouchState) {
        val fingerCount = recognizeFingerCount(maxDownState)
        val focusArea = touchCenterArea(maxDownState)
        listener.onSingleTap(fingerCount, focusArea)
        this.state = State.None
    }

    private fun startTapRepeat(onTapListener: OnTapListener) {
        this.state = State.Tap(onTapListener)
    }

    private fun endTapRepeat(state: State.Tap) {
        state.listener.onEnd()
        this.state = State.None
    }

    private fun awaitScroll(downState: TouchState, maxDownState: TouchState) {
        this.state = State.AwaitScroll(downState, maxDownState)
    }

    private fun startScrollOrPinch(downState: TouchState, currentState: TouchState) {
        when {
            isInOneDirection(downState, currentState) -> startScroll(downState, currentState)
            else -> startPinch(downState, currentState)
        }
    }

    private fun startScroll(downState: TouchState, currentState: TouchState) {
        val downCenter = touchCenterArea(downState)
        val currentCenter = touchCenter(currentState)
        val delta = PositionF(currentCenter.x - downCenter.x, currentCenter.y - downCenter.y)
        val fingerCount = recognizeFingerCount(downState)
        val direction = recognizeDirection(delta.x, delta.y)
        val onScrollListener = listener.onScroll(fingerCount, direction, downCenter)
        onScrollListener.onScroll(delta)
        this.state = State.Scroll(onScrollListener, currentState)
    }

    private fun scroll(state: State.Scroll, touchState: TouchState) {
        val lastCenter = touchCenter(state.lastState)
        val currentCenter = touchCenter(touchState)
        val delta = PositionF(currentCenter.x - lastCenter.x, currentCenter.y - lastCenter.y)
        if (delta.x != 0F || delta.y != 0F) {
            state.listener.onScroll(delta)
            state.lastState = touchState
        }
    }

    private fun endScroll(state: State.Scroll, velocity: PositionF) {
        state.listener.onEnd(velocity)
        this.state = State.None
    }

    private fun startPinch(downState: TouchState, currentState: TouchState) {
        val downRadius = touchRadius(downState)
        val currentRadius = touchRadius(currentState)
        val radiusDelta = currentRadius - downRadius
        val pinchDirection = recognizePinchDirection(radiusDelta)
        val onPinchListener = listener.onPinch(pinchDirection)
        onPinchListener.onPinch(radiusDelta)
        this.state = State.Pinch(onPinchListener, currentState)
    }

    private fun pinch(state: State.Pinch, touchState: TouchState) {
        val lastRadius = touchRadius(state.lastState)
        val currentRadius = touchRadius(touchState)
        val radiusDelta = currentRadius - lastRadius
        if (radiusDelta != 0F) {
            state.listener.onPinch(radiusDelta)
            state.lastState = touchState
        }
    }

    private fun endPinch(state: State.Pinch) {
        state.listener.onEnd()
        this.state = State.None
    }

    private fun delay(millis: Long, action: () -> Unit) {
        launch(context, parent = stateJob) {
            delay(millis)
            action()
        }
    }

    fun cancel() {
        val state = state
        when (state) {
            is State.Tap -> state.listener.onCancel()
            is State.Scroll -> state.listener.onCancel()
            is State.Pinch -> state.listener.onCancel()
        }
        this.state = State.None
        velocityTrackerFingerCount = 0
        velocityTracker.clear()
    }

    private fun isInOneDirection(oldState: TouchState, newState: TouchState): Boolean {
        require(oldState.fingerCount == newState.fingerCount)
        if (newState.fingerCount == 0)
            return true
        val firstX = newState.fingers[0].x - oldState.fingers[0].x
        val firstY = newState.fingers[0].y - oldState.fingers[0].y
        for (i in 1 until newState.fingerCount) {
            val x = newState.fingers[i].x - oldState.fingers[i].x
            val y = newState.fingers[i].y - oldState.fingers[i].y
            val dot = firstX * x + firstY * y
            if (dot < 0)
                return false
        }
        return true
    }

    private fun recognizeDirection(vectorX: Float, vectorY: Float) = when {
        vectorX < 0 && abs(vectorX) > abs(vectorY) -> Direction.LEFT
        vectorX >= 0 && abs(vectorX) > abs(vectorY) -> Direction.RIGHT
        vectorY < 0 && abs(vectorX) <= abs(vectorY) -> Direction.UP
        else -> Direction.DOWN
    }

    private fun recognizePinchDirection(radiusDelta: Float) = if (radiusDelta >= 0) {
        PinchDirection.OUT
    } else {
        PinchDirection.IN
    }

    private fun recognizeFingerCount(downState: TouchState) = if (downState.fingerCount >= 2) FingerCount.MULTIPLE else FingerCount.SINGLE

    private sealed class State {
        object None : State()
        class AwaitLongTap(var lastDownState: TouchState, val maxDownState: TouchState) : State()
        class AwaitScroll(var lastDownState: TouchState, val maxDownState: TouchState) : State()
        class AwaitSecondTap(val maxDownState: TouchState) : State()
        class Tap(val listener: OnTapListener) : State()
        class Scroll(val listener: OnScrollListener, var lastState: TouchState) : State()
        class Pinch(val listener: OnPinchListener, var lastState: TouchState) : State()
    }

    enum class Direction { LEFT, RIGHT, UP, DOWN }
    enum class PinchDirection { IN, OUT }
    enum class FingerCount { SINGLE, MULTIPLE }
}