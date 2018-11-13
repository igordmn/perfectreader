package com.dmi.perfectreader.ui.book.gl

import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.ui.book.render.obj.RenderObject
import com.dmi.perfectreader.ui.book.render.obj.RenderPage
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.collection.Pool
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope

class GLPage(
        val page: Page,
        private val model: GLBookModel,
        private val quad: GLQuad,
        private val texturePool: Pool<GLTexture>,
        private val refresher: GLPageRefresher,
        private val scope: Scope = Scope()
) : Disposable by scope {
    private val canvasTexture: GLTexture by scope.observableDisposable(
            texturePool.acquire(),
            dispose = {
                texturePool.release(canvasTexture)
            }
    )
    private var previousContext: RenderObject.Context? = null
    private val context: RenderObject.Context by scope.cached { RenderObject.Context(model.selection) }
    private val renderPage: RenderPage? by scope.async { refresher.render(page) }
    private val loadingTexture: GLTexture? by scope.async(resetOnRecompute = false) {
        renderPage?.let { renderPage ->
            refresher.refresh(renderPage, canvasTexture, previousContext, context)
            previousContext = context
            canvasTexture
        }
    }

    fun draw() {
        loadingTexture?.let(quad::draw)
    }
}