package com.dmi.util.opengl

import com.dmi.util.graphic.Size

interface FixedRenderer {
    fun destroy() = Unit
    fun draw() = Unit
}

class FixedRendererWrapper(private val create: (Size) -> FixedRenderer) : GLSurfaceViewExt.RendererExt {
    private var renderer: FixedRenderer? = null

    override fun onSurfaceCreated() = Unit

    override fun onSurfaceChanged(size: Size) {
        if (renderer == null && size.width > 0 && size.height > 0) {
            renderer = create(size)
        }
    }

    override fun onSurfaceDestroyed() {
        renderer?.destroy()
        renderer = null
    }

    override fun onDrawFrame() {
        renderer?.draw()
    }
}