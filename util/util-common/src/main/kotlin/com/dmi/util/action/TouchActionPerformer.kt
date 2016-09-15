package com.dmi.util.action

import com.dmi.util.graphic.PositionF
import com.dmi.util.input.GestureDetector
import com.dmi.util.input.GestureDetector.*
import com.dmi.util.input.TouchArea

class TouchActionPerformer(private val provider: Provider) : GestureDetector.Listener {
    interface Provider {
        fun singleTap(area: TouchArea): Action
        fun longTap(area: TouchArea): Action
        fun doubleTap(area: TouchArea): Action
        fun twoFingersSingleTap(area: TouchArea): Action
        fun twoFingersLongTap(area: TouchArea): Action
        fun twoFingersDoubleTap(area: TouchArea): Action

        fun scrollLeft(area: TouchArea): Action
        fun scrollRight(area: TouchArea): Action
        fun scrollUp(area: TouchArea): Action
        fun scrollDown(area: TouchArea): Action
        fun twoFingersScrollLeft(area: TouchArea): Action
        fun twoFingersScrollRight(area: TouchArea): Action
        fun twoFingersScrollUp(area: TouchArea): Action
        fun twoFingersScrollDown(area: TouchArea): Action

        fun twoFingersPinchIn(): Action
        fun twoFingersPinchOut(): Action
    }

    override fun onSingleTap(fingerCount: FingerCount, focusArea: TouchArea) {
        val action = if (fingerCount == FingerCount.SINGLE) provider.singleTap(focusArea) else provider.twoFingersSingleTap(focusArea)
        action.perform()
        action.touch(focusArea)
    }

    override fun onLongTap(fingerCount: FingerCount, focusArea: TouchArea) = object : OnTapListener {
        val action = if (fingerCount == FingerCount.SINGLE) provider.longTap(focusArea) else provider.twoFingersLongTap(focusArea)

        init {
            action.perform()
            action.touch(focusArea)
            action.startTap()
        }

        override fun onEnd() = action.endTap()
        override fun onCancel() = action.endTap()
    }

    override fun onDoubleTap(fingerCount: FingerCount, focusArea: TouchArea) = object : OnTapListener {
        val action = if (fingerCount == FingerCount.SINGLE) provider.doubleTap(focusArea) else provider.twoFingersDoubleTap(focusArea)

        init {
            action.perform()
            action.touch(focusArea)
            action.startTap()
        }

        override fun onEnd() = action.endTap()
        override fun onCancel() = action.endTap()
    }

    override fun onScroll(fingerCount: FingerCount, direction: Direction, startArea: TouchArea) = object : OnScrollListener {
        val action = when {
            fingerCount == FingerCount.SINGLE && direction == Direction.LEFT -> provider.scrollLeft(startArea)
            fingerCount == FingerCount.SINGLE && direction == Direction.RIGHT -> provider.scrollRight(startArea)
            fingerCount == FingerCount.SINGLE && direction == Direction.UP -> provider.scrollUp(startArea)
            fingerCount == FingerCount.SINGLE && direction == Direction.DOWN -> provider.scrollDown(startArea)
            fingerCount == FingerCount.MULTIPLE && direction == Direction.LEFT -> provider.twoFingersScrollLeft(startArea)
            fingerCount == FingerCount.MULTIPLE && direction == Direction.RIGHT -> provider.twoFingersScrollRight(startArea)
            fingerCount == FingerCount.MULTIPLE && direction == Direction.UP -> provider.twoFingersScrollUp(startArea)
            fingerCount == FingerCount.MULTIPLE && direction == Direction.DOWN -> provider.twoFingersScrollDown(startArea)
            else -> throw IllegalStateException()
        }
        val isHorizontal = direction == Direction.LEFT || direction == Direction.RIGHT

        init {
            action.perform()
            action.startChange()
            action.startScroll()
        }

        override fun onScroll(delta: PositionF) {
            action.change(if (isHorizontal) delta.x else delta.y)
            action.scroll(delta)
        }

        override fun onEnd(velocity: PositionF) {
            action.endScroll(velocity)
            action.endChange()
        }

        override fun onCancel() {
            action.cancelScroll()
            action.endChange()
        }
    }

    override fun onPinch(direction: PinchDirection) = object : OnPinchListener {
        val action = if (direction == PinchDirection.IN) provider.twoFingersPinchIn() else provider.twoFingersPinchOut()

        init {
            action.perform()
            action.startChange()
        }

        override fun onPinch(radiusDelta: Float) = action.change(radiusDelta)
        override fun onEnd() = action.endChange()
        override fun onCancel() = action.endChange()
    }
}