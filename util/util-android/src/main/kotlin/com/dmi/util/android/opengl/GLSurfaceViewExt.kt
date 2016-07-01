package com.dmi.util.android.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import com.dmi.util.graphic.Size
import com.dmi.util.log
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewExt(context: Context) : GLSurfaceView(context) {
    private var rendererExt: RendererExt? = null

    private val eglContextClientVersion = 2

    init {
        super.setEGLContextClientVersion(eglContextClientVersion)
        super.setEGLContextFactory(DefaultContextFactory())
    }

    fun setRenderer(renderer: RendererExt) {
        require(rendererExt == null)
        rendererExt = renderer
        super.setRenderer(RendererExtWrapper(renderer))
    }

    override fun setEGLContextFactory(factory: EGLContextFactory) = throw UnsupportedOperationException()
    override fun setEGLContextClientVersion(version: Int) = throw UnsupportedOperationException()
    override fun setRenderer(renderer: Renderer) = throw UnsupportedOperationException()

    interface RendererExt {
        fun onSurfaceCreated() = Unit
        fun onSurfaceChanged(size: Size) = Unit
        fun onSurfaceDestroyed() = Unit
        fun onDrawFrame() = Unit
    }

    class RendererExtWrapper(private val rendererExt: RendererExt) : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) = rendererExt.onSurfaceCreated()
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) = rendererExt.onSurfaceChanged(Size(width, height))
        override fun onDrawFrame(gl: GL10) = rendererExt.onDrawFrame()
    }

    private inner class DefaultContextFactory : EGLContextFactory {
        private val EGL_CONTEXT_CLIENT_VERSION = 0x3098

        override fun createContext(egl: EGL10, display: EGLDisplay, config: EGLConfig): EGLContext {
            val attributes = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, eglContextClientVersion, EGL10.EGL_NONE)
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attributes)
        }

        override fun destroyContext(egl: EGL10, display: EGLDisplay,
                                    context: EGLContext) {
            rendererExt?.onSurfaceDestroyed()

            if (!egl.eglDestroyContext(display, context)) {
                log.e("DefaultContextFactory display:$display context: $context")
                val errorString = Graphics.getErrorString(egl.eglGetError())
                error("eglDestroyContext failed: $errorString")
            }
        }
    }
}