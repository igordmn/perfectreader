package com.dmi.perfectreader.fragment.book.page

import android.graphics.PorterDuff
import com.dmi.perfectreader.app.glBackgroundScheduler
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.PageRenderer
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.GLTexturePlane
import com.dmi.util.collection.Pool
import com.dmi.util.concurrent.SingleResourceLoader
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.Rect

class GLPage(
        val page: Page,
        pageContext: PageContext,
        private val texturePool: Pool<GLTexture>,
        private val texturePlane: GLTexturePlane,
        private val background: GLPageBackground,
        private val bitmapBufferPool: Pool<BitmapBuffer>,
        private val pageRenderer: PageRenderer
) {
    private var texture = texturePool.acquire()
    private var refreshed = false

    private val bitmapLoader = SingleResourceLoader<RenderResult>(glBackgroundScheduler, destroyResult = { releaseBuffer(it) })
    private var renderPage: RenderPage? = null

    var pageContext: PageContext = pageContext
        set(value) {
            scheduleRender(field, value)
            field = value
        }

    val onChanged = bitmapLoader.onReady

    init {
        scheduleRender(null, pageContext)
    }

    private fun scheduleRender(oldContext: PageContext?, newContext: PageContext) {
        if (oldContext !== newContext) {
            bitmapLoader.schedule {
                render(newContext, oldContext)
            }
        }
    }

    private fun render(newContext: PageContext, oldContext: PageContext?): RenderResult {
        val renderPage = renderPage()
        val dirtyRect = renderPage.dirtyRect(oldContext, newContext)
        return if (dirtyRect.isEmpty) {
            RenderResult(null, dirtyRect)
        } else {
            val buffer = bitmapBufferPool.acquire()
            val canvas = buffer.canvas
            canvas.drawColor(Color.TRANSPARENT.value, PorterDuff.Mode.CLEAR)
            renderPage.paint(newContext, canvas, dirtyRect)
            RenderResult(buffer, dirtyRect)
        }
    }

    private fun renderPage(): RenderPage {
        if (renderPage == null)
            renderPage = pageRenderer.render(page)
        return renderPage!!
    }

    private fun releaseBuffer(renderResult: RenderResult) {
        renderResult.buffer?.let {
            bitmapBufferPool.release(it)
        }
    }

    fun destroy() {
        bitmapLoader.destroy()
        texturePool.release(texture)
    }

    fun draw(matrix: FloatArray) {
        background.draw(matrix)
        bitmapLoader.completeIfReady { result ->
            if (result.buffer != null) {
                texture.refreshBy(result.buffer.bitmap, result.dirtyRect)
                refreshed = true
            }
        }
        if (refreshed)
            texturePlane.draw(matrix, texture)
    }

    private class RenderResult(val buffer: BitmapBuffer?, val dirtyRect: Rect)
}