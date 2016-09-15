package com.dmi.util.action

import com.dmi.util.ext.repeat
import com.dmi.util.graphic.PositionF
import com.dmi.util.input.TouchArea
import rx.Scheduler
import rx.Subscription

interface Action {
    fun startScroll(area: TouchArea) = Unit
    fun scroll(delta: PositionF) = Unit
    fun endScroll(velocity: PositionF) = Unit
    fun cancelScroll() = Unit

    fun perform() = Unit
    fun startTap() = Unit
    fun endTap() = Unit

    fun touch(area: TouchArea) = Unit

    fun startChange() = Unit
    fun change(delta: Float) = Unit
    fun endChange() = Unit
}

object NoneAction : Action

fun performAction(perform: () -> Unit) = object : Action {
    override fun perform() = perform()
}

fun touchAction(touch: (area: TouchArea) -> Unit) = object : Action {
    override fun touch(area: TouchArea) = touch(area)
}

abstract class RepeatAction(private val scheduler: Scheduler, private val periodMillis: Long) : Action {
    private var subscription: Subscription? = null

    override fun startTap() {
        if (subscription == null)
            subscription = repeat(periodMillis, scheduler) { perform() }
    }

    override fun endTap() {
        subscription?.unsubscribe()
        subscription = null
    }
}