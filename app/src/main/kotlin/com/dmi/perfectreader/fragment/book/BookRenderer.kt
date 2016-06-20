package com.dmi.perfectreader.fragment.book

import android.opengl.GLES20.*
import com.dmi.perfectreader.fragment.book.page.PagesRenderer
import com.dmi.util.ext.merge
import com.dmi.util.graphic.Size
import com.dmi.util.opengl.NotifiableRenderer
import com.dmi.util.refWatcher
import com.dmi.util.system.ThreadPriority
import com.dmi.util.system.setPriority
import rx.Observable
import java.lang.Thread.currentThread

class BookRenderer(
        size: Size,
        private val model: BookRenderModel,
        private val createPages: () -> PagesRenderer
) : NotifiableRenderer {
    private val pages: PagesRenderer

    override val onNeedDraw: Observable<Unit>

    init {
        currentThread().setPriority(ThreadPriority.DISPLAY)

        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glViewport(0, 0, size.width, size.height)

        pages = createPages()

        onNeedDraw = pages.onNeedDraw merge model.onNeedUpdate
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
    }
}