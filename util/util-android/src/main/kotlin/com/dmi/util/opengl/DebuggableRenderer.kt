package com.dmi.util.opengl

import com.dmi.util.log
import rx.Observable

class DebuggableRenderer(
        private val thresholdMillis: Int,
        private val renderer: NotifiableRenderer
): NotifiableRenderer {
    override val onNeedDraw: Observable<Unit> get() = renderer.onNeedDraw

    private val freezeWatcher = FreezeWatcher(renderer.javaClass.simpleName, thresholdMillis)

    private val subscription = renderer.onNeedDraw.subscribe {
        freezeWatcher.onRenderRequest()
    }

    override fun destroy() {
        subscription.unsubscribe()
        renderer.destroy()
    }

    override fun draw() {
        freezeWatcher.onBeginRender()
        renderer.draw()
        freezeWatcher.onEndRender()
    }
}

private class FreezeWatcher(private val name: String, private val thresholdMillis: Int) {
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