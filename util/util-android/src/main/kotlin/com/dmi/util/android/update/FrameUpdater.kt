package com.dmi.util.android.update

import android.view.Choreographer

abstract class FrameUpdater {
    private var frameUpdateScheduled = false
    private val frameCallback = Choreographer.FrameCallback {
        frameUpdateScheduled = false
        update()
    }

    protected abstract fun update()

    fun scheduleUpdate() {
        if (!frameUpdateScheduled) {
            frameUpdateScheduled = true
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }

    fun cancel() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }
}