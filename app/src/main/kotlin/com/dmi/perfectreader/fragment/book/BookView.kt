package com.dmi.perfectreader.fragment.book

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.GLSurfaceView
import android.widget.FrameLayout
import com.dmi.perfectreader.BuildConfig.DEBUG_SHOWRENDERFREEZES
import com.dmi.util.base.BaseView
import com.dmi.util.base.px2dip
import com.dmi.util.graphic.Size
import com.dmi.util.opengl.DebuggableRenderer
import com.dmi.util.opengl.GLSurfaceViewExt
import com.dmi.util.opengl.setRenderer
import com.dmi.util.system.ActivityLifeCycle
import com.dmi.util.widget.onSizeChange

class BookView(
        context: Context,
        private val model: Book,
        private val createRenderer: (Size) -> BookRenderer,
        private val activityLifeCycle: ActivityLifeCycle
) : BaseView(FrameLayout(context)) {
    private val glSurface = GLSurfaceViewExt(context)

    init {
        /*
         * Фикс бага с анимацией.
         * Без этого не работает анимация исчезновения меню.
         * Решение найдено здесь:
         * http://stackoverflow.com/questions/14925060/ugly-fragment-transition-to-surfaceview-with-overlay
         */
        glSurface.background = ColorDrawable(Color.TRANSPARENT)

        widget.addView(glSurface)
        widget.keepScreenOn = true
        glSurface.setRenderer { size ->
            if (DEBUG_SHOWRENDERFREEZES) {
                DebuggableRenderer(thresholdMillis = 40, renderer = createRenderer(size))
            } else {
                createRenderer(size)
            }
        }
        glSurface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurface.onSizeChange { size, oldSize ->
            model.resize(px2dip(size.toFloat()))
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