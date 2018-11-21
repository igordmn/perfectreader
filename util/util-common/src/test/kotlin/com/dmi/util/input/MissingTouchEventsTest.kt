package com.dmi.util.input

import com.dmi.test.shouldBe
import org.junit.Test

class MissingTouchEventsTest {
    @Test
    fun `two fingers after empty`() {
        val events = MissingTouchEvents()
        events.collectOnTouch(
                event(TouchAction.DOWN, 2)
        ) shouldBe listOf(
                event(TouchAction.DOWN, 1),
                event(TouchAction.DOWN, 2)
        )
    }

    @Test
    fun `four fingers after one`() {
        val events = MissingTouchEvents()
        events.collectOnTouch(event(TouchAction.DOWN, 1)) shouldBe listOf(event(TouchAction.DOWN, 1))
        events.collectOnTouch(event(TouchAction.DOWN, 4)) shouldBe listOf(
                event(TouchAction.DOWN, 2),
                event(TouchAction.DOWN, 3),
                event(TouchAction.DOWN, 4)
        )
    }

    @Test
    fun `empty fingers after two`() {
        val events = MissingTouchEvents()
        events.collectOnTouch(event(TouchAction.DOWN, 1)) shouldBe listOf(event(TouchAction.DOWN, 1))
        events.collectOnTouch(event(TouchAction.DOWN, 2)) shouldBe listOf(event(TouchAction.DOWN, 2))
        events.collectOnTouch(event(TouchAction.UP, 0)) shouldBe listOf(
                event(TouchAction.UP, 1),
                event(TouchAction.UP, 0)
        )
    }


    @Test
    fun `two fingers after four`() {
        val events = MissingTouchEvents()
        events.collectOnTouch(event(TouchAction.DOWN, 1)) shouldBe listOf(event(TouchAction.DOWN, 1))
        events.collectOnTouch(event(TouchAction.DOWN, 2)) shouldBe listOf(event(TouchAction.DOWN, 2))
        events.collectOnTouch(event(TouchAction.DOWN, 4)) shouldBe listOf(event(TouchAction.DOWN, 3), event(TouchAction.DOWN, 4))
        events.collectOnTouch(event(TouchAction.UP, 2)) shouldBe listOf(
                event(TouchAction.UP, 3),
                event(TouchAction.UP, 2)
        )
    }

    fun MissingTouchEvents.collectOnTouch(event: TouchEvent): List<TouchEvent> {
        val list = ArrayList<TouchEvent>()
        onTouch(event) {
            list.add(it)
        }
        return list
    }

    private fun event(action: TouchAction, fingerCount: Int) = TouchEvent(action, state(fingerCount), 0L)
    private fun state(fingerCount: Int) = TouchState((0 until fingerCount).map { TouchArea(10F + it, 11F + it, 5F) }.toTypedArray())
}