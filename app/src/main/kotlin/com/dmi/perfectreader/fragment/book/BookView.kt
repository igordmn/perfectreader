package com.dmi.perfectreader.fragment.book

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.GLSurfaceView
import android.widget.FrameLayout
import com.dmi.perfectreader.BuildConfig.DEBUG_SHOWRENDERFREEZES
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.px2dip
import com.dmi.util.android.opengl.DebuggableRenderer
import com.dmi.util.android.opengl.GLSurfaceViewExt
import com.dmi.util.android.opengl.setNotifiableRenderer
import com.dmi.util.android.system.ActivityLifeCycle
import com.dmi.util.android.widget.onSizeChange
import com.dmi.util.graphic.Size

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
        glSurface.setNotifiableRenderer { size ->
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