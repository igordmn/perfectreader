package com.dmi.util.debug

import com.dmi.util.log

class RenderFreezeWatcher(private val name: String, private val thresholdMillis: Int) {
    private @Volatile var requestedNewFrame = false
    private var lastTime = -1L

    fun onRenderRequest() {
        requestedNewFrame = true
    }

    fun onBeginRender() {
        requestedNewFrame = false
        val now = System.nanoTime()
        if (lastTime != -1L) {
            val deltaMillis = ((now - lastTime) / 1.0E6).toInt()
            if (deltaMillis > thresholdMillis)
                log.d("$name freezes $deltaMillis ms")
        }
        lastTime = now
    }

    fun onEndRender() {
        if (!requestedNewFrame)
            lastTime = -1L
    }
}