package com.dmi.util.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

import java.util.concurrent.atomic.AtomicBoolean

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class GLSurfaceViewExt : GLSurfaceView {
    private var renderer: GLRenderer? = null

    private val renderRun = AtomicBoolean(false)
    private var needFreeResources = false

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    override fun onPause() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                queueEvent { this.freeResources() }
                super.onPause()
            }
        }
    }

    override fun onResume() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                super.onResume()
            }
        }
    }

    override fun onDetachedFromWindow() {
        queueEvent { this.freeResources() }
        super.onDetachedFromWindow()
    }

    override fun requestRender() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                super.requestRender()
            }
        }
    }

    fun setRenderer(renderer: GLRenderer) {
        this.renderer = renderer
        synchronized (renderRun) {
            super.setRenderer(object : GLSurfaceView.Renderer {
                override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                    needFreeResources = true
                    renderer.onSurfaceCreated()
                }

                override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
                    renderer.onSurfaceChanged(width, height)
                }

                override fun onDrawFrame(gl: GL10) {
                    renderer.onDrawFrame()
                }
            })
            renderRun.set(true)
        }
    }

    private fun freeResources() {
        if (needFreeResources) {
            renderer!!.onFreeResources()
            needFreeResources = false
        }
    }

    override fun setRenderer(renderer: GLSurfaceView.Renderer) {
        throw RuntimeException("You should use setRenderer(GLRenderer)")
    }
}
