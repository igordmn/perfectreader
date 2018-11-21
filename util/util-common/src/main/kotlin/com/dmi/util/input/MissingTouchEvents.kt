package com.dmi.util.input

import java.util.*
import kotlin.math.abs

/**
 * If multiple-finger event occurred without less-finger event, emit missing events
 */
class MissingTouchEvents {
    private var lastState = TouchState(emptyArray())

    fun onTouch(event: TouchEvent, delegate: (TouchEvent) -> Unit) {
        fun onDown() {
            val fingers = event.state.fingers.take(lastState.fingerCount).toMutableList()
            for (i in lastState.fingerCount until event.state.fingerCount) {
                fingers.add(event.state.fingers[i])
                val state = TouchState(fingers.toTypedArray())
                delegate(event.copy(state = state))
            }
        }

        fun onUp() {
            val fingers = LinkedList(lastState.fingers.toList())
            for (i in event.state.fingerCount until lastState.fingerCount) {
                fingers.removeLast()
                val state = TouchState(fingers.toTypedArray())
                delegate(event.copy(state = state))
            }
        }

        if (abs(event.state.fingerCount - lastState.fingerCount) <= 1) {
            delegate(event)
        } else {
            when (event.action) {
                TouchAction.DOWN -> onDown()
                TouchAction.UP -> onUp()
                TouchAction.MOVE -> delegate(event)
            }
        }
        lastState = event.state
    }
}