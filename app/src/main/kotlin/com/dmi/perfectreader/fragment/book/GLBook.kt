package com.dmi.perfectreader.fragment.book

import android.opengl.GLES20.*
import com.dmi.perfectreader.fragment.book.page.GLPages
import com.dmi.perfectreader.fragment.book.page.GLRefreshScheduler
import com.dmi.util.android.opengl.NotifiableRenderer
import com.dmi.util.android.system.ThreadPriority
import com.dmi.util.android.system.setPriority
import com.dmi.util.ext.merge
import com.dmi.util.graphic.Size
import com.dmi.util.refWatcher
import rx.Observable
import java.lang.Thread.currentThread

class GLBook(
        size: Size,
        private val model: BookRenderModel,
        createPages: () -> GLPages,
        private val refreshScheduler: GLRefreshScheduler
) : NotifiableRenderer {
    private val pages: GLPages

    override val onNeedDraw: Observable<Unit>

    init {
        currentThread().setPriority(ThreadPriority.DISPLAY)

        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glViewport(0, 0, size.width, size.height)

        pages = createPages()

        onNeedDraw = pages.onNeedDraw merge model.onChanged
    }

    override fun destroy() {
        pages.destroy()
        refWatcher.watch(this)
    }

    override fun draw() {
        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT)

        model.update()
        pages.draw(model.pages)
        refreshScheduler.refresh()
    }
}