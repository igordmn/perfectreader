package com.dmi.util.opengl

class GLRendererDelegate(private val renderer: GLRenderer) : GLRenderer {

    override fun onSurfaceCreated() {
        renderer.onSurfaceCreated()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        renderer.onSurfaceChanged(width, height)
    }

    override fun onFreeResources() {
        renderer.onFreeResources()
    }

    override fun onDrawFrame() {
        renderer.onDrawFrame()
    }
}
