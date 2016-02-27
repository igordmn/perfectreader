package com.dmi.util.opengl

import android.content.Context
import android.util.AttributeSet

import java.lang.Math.min

abstract class DeltaTimeGLSurfaceView : GLSurfaceViewExt {

    private var previewTime: Long = -1
    private val averageDeltaTime = AverageValue(SMOOTH_SAMPLES)

    protected constructor(context: Context) : super(context) {
    }

    protected constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    protected fun runRender() {
        setRenderer(DeltaTimeRenderer())
    }

    fun resetTimer() {
        previewTime = -1
    }

    protected open fun onSurfaceCreated() {
    }

    protected open fun onSurfaceChanged(width: Int, height: Int) {
    }

    protected open fun onFreeResources() {
    }

    protected open fun onUpdate(dt: Float) {
    }

    protected open fun onDrawFrame() {
    }

    private inner class DeltaTimeRenderer : GLRenderer {
        override fun onSurfaceCreated() {
            this@DeltaTimeGLSurfaceView.onSurfaceCreated()
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
            this@DeltaTimeGLSurfaceView.onSurfaceChanged(width, height)
        }

        override fun onFreeResources() {
            this@DeltaTimeGLSurfaceView.onFreeResources()
        }

        override fun onDrawFrame() {
            this@DeltaTimeGLSurfaceView.onUpdate(deltaTimeSeconds())
            this@DeltaTimeGLSurfaceView.onDrawFrame()
        }
    }

    private fun deltaTimeSeconds(): Float {
        val nowTime = System.nanoTime()
        if (previewTime != -1L) {
            val deltaTimeSeconds = min((nowTime - previewTime) / 1E9f, MAX_DELTA_TIME_SECONDS)
            averageDeltaTime.put(deltaTimeSeconds)
        } else {
            averageDeltaTime.reset()
        }
        previewTime = nowTime
        return averageDeltaTime.average()
    }

    private class AverageValue constructor(private val samples: Int) {
        private val values: FloatArray

        private var offset = 0
        private var count = 0

        init {
            values = FloatArray(samples)
        }

        fun put(value: Float) {
            values[offset++] = value
            if (offset == samples) {
                offset = 0
            }
            if (count < samples) {
                count++
            }
        }

        fun reset() {
            offset = 0
            count = 0
        }

        fun average(): Float {
            var sum = 0f
            for (i in 0..count - 1) {
                sum += values[i]
            }
            return if (count > 0) sum / count else 0F
        }
    }

    companion object {
        private val SMOOTH_SAMPLES = 8
        private val MAX_DELTA_TIME_SECONDS = 1 / 20.0f
    }
}
