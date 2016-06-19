package com.dmi.perfectreader.fragment.book

import android.opengl.GLES20.*
import com.dmi.perfectreader.BuildConfig.DEBUG_SHOWRENDERFREEZES
import com.dmi.perfectreader.fragment.book.page.PagesRenderer
import com.dmi.util.debug.RenderFreezeWatcher
import com.dmi.util.graphic.Size
import com.dmi.util.opengl.FixedRenderer
import com.dmi.util.opengl.GLSurfaceViewExt
import com.dmi.util.system.ThreadPriority
import com.dmi.util.system.setPriority
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.lang.Thread.currentThread

class BookRenderer(
        size: Size,
        private val model: BookRenderModel,
        surface: GLSurfaceViewExt,
        private val createPages: () -> PagesRenderer

) : FixedRenderer {
    private val freezeWatcher = RenderFreezeWatcher(name = "BookRenderer", thresholdMillis = 40)
    private val subscriptions = CompositeSubscription()
    private val pages: PagesRenderer

    init {
        currentThread().setPriority(ThreadPriority.DISPLAY)

        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glViewport(0, 0, size.width, size.height)

        pages = createPages()

        fun Observable<*>.subscribeRequestRender() = subscriptions.add(
                subscribe {
                    if (DEBUG_SHOWRENDERFREEZES) freezeWatcher.onRenderRequest()
                    surface.requestRender()
                }
        )

        pages.onNeedDraw.subscribeRequestRender()
        model.onNeedUpdate.subscribeRequestRender()
    }

    override fun destroy() {
        subscriptions.clear()
        pages.destroy()
    }

    override fun draw() {
        if (DEBUG_SHOWRENDERFREEZES) freezeWatcher.onBeginRender()

        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT)

        model.update()
        pages.draw(model.pages)

        if (DEBUG_SHOWRENDERFREEZES) freezeWatcher.onEndRender()
    }
}