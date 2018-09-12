package com.dmi.util.android.opengl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.widget.FrameLayout
import com.dmi.util.coroutine.initThreadContext
import com.dmi.util.graphic.Size
import com.dmi.util.log.Log
import com.dmi.util.scope.CopyScope
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.onchange
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10
import kotlin.coroutines.CoroutineContext

@SuppressLint("ViewConstructor")
class GLSurfaceScopedView(
        context: Context,
        private val log: Log,
        private val createRenderer: (CopyScope) -> ((Size) -> Renderer)
) : FrameLayout(context) {
    private val eglContextClientVersion = 2
    private val glSurfaceView = GLSurfaceView(context)

    private val coroutineContext: CoroutineContext = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) = glSurfaceView.queueEvent(block)
    }

    private var scope: CopyScope
    private var createRendererScoped: ((Size) -> Renderer)? = null
    private val job = Job()

    private var initComplete = false
    private val afterInit = ArrayList<() -> Unit>()

    private var renderer: Renderer? = null

    init {
        addView(glSurfaceView)
        /*
         * Фикс бага с анимацией.
         * Без этого не работает анимация исчезновения меню.
         * Решение найдено здесь:
         * http://stackoverflow.com/questions/14925060/ugly-fragment-transition-to-surfaceview-with-overlay
         */
        glSurfaceView.background = ColorDrawable(Color.TRANSPARENT)
        glSurfaceView.setEGLContextClientVersion(eglContextClientVersion)
        glSurfaceView.setEGLContextFactory(DefaultContextFactory())
        glSurfaceView.setRenderer(OriginalRenderer())
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        scope = CopyScope(coroutineContext, UI)

        glSurfaceView.queueEvent {
            // if we there, then scope already initialized

            initThreadContext(coroutineContext)
            launch(UI, parent = job) {
                initUI()
            }
        }
    }

    private fun initUI() {
        createRendererScoped = createRenderer(scope)
        glSurfaceView.queueEvent { initGL() }
    }

    private fun initGL() {
        initComplete = true
        afterInit.forEach { it() }
        afterInit.clear()
        glSurfaceView.requestRender()
    }

    fun onPause() = glSurfaceView.onPause()
    fun onResume() = glSurfaceView.onResume()

    private var detached = false

    override fun onAttachedToWindow() {
        // it is hard to implement because gl thread restarted on reattach (need recreate glModel, initThreadContext, etc)
        check(!detached) { "Reattaching isn't supported" }
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        job.cancel()
        scope.dispose()
        detached = true
        super.onDetachedFromWindow()
    }

    interface Renderer : Disposable {
        /**
         * All scoped values will be intercepted. When any of this variables changed, draw will be called automatically again
         */
        fun draw()
    }

    private inner class OriginalRenderer : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            glClearColor(1F, 1F, 1F, 1F)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            fun init() {
                renderer?.dispose()
                renderer = createRendererScoped!!(Size(width, height))
            }

            if (initComplete) {
                init()
            } else {
                afterInit.add(::init)
            }
        }

        override fun onDrawFrame(gl: GL10) {
            onchange {
                renderer?.draw()
            }.subscribeOnce {
                glSurfaceView.requestRender()
            }
        }
    }

    private inner class DefaultContextFactory : GLSurfaceView.EGLContextFactory {
        private val eglContextClientVersionTag = 0x3098

        override fun createContext(egl: EGL10, display: EGLDisplay, config: EGLConfig): EGLContext {
            val attributes = intArrayOf(eglContextClientVersionTag, eglContextClientVersion, EGL10.EGL_NONE)
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attributes)
        }

        override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
            afterInit.clear()
            renderer?.dispose()
            renderer = null

            if (!egl.eglDestroyContext(display, context)) {
                log.e("DefaultContextFactory display:$display context: $context")
                val errorString = getEGLErrorString(egl.eglGetError())
                error("eglDestroyContext failed: $errorString")
            }
        }
    }
}