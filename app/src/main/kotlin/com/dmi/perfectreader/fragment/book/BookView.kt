package com.dmi.perfectreader.fragment.book

import android.content.Context
import android.opengl.GLSurfaceView
import android.widget.FrameLayout
import com.dmi.perfectreader.BuildConfig.DEBUG_SHOWRENDERFREEZES
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.opengl.DebuggableRenderer
import com.dmi.util.android.opengl.GLSurfaceViewExt
import com.dmi.util.android.opengl.setNotifiableRenderer
import com.dmi.util.android.system.ActivityLifeCycle
import com.dmi.util.android.widget.onSizeChange
import com.dmi.util.graphic.Size

class BookView(
        context: Context,
        private val model: Book,
        private val createGLBook: (Size) -> GLBook,
        activityLifeCycle: ActivityLifeCycle
) : BaseView(FrameLayout(context)) {
    private val glSurface = GLSurfaceViewExt(context)

    init {
        widget.addView(glSurface)
        widget.keepScreenOn = true
        glSurface.setNotifiableRenderer { size ->
            if (DEBUG_SHOWRENDERFREEZES) {
                DebuggableRenderer(thresholdMillis = 40, renderer = createGLBook(size))
            } else {
                createGLBook(size)
            }
        }
        glSurface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurface.onSizeChange { size, oldSize ->
            model.resize(size.toFloat())
        }
        subscribe(activityLifeCycle.isResumedObservable) {
            if (it) {
                glSurface.onResume()
            } else {
                glSurface.onPause()
            }
        }
    }
}