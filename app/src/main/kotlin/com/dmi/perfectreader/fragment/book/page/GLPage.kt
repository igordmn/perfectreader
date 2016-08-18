package com.dmi.perfectreader.fragment.book.page

import android.graphics.PorterDuff
import com.dmi.perfectreader.app.glBackgroundScheduler
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.factory.PageRenderer
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
    private var renderedPageContext: PageContext? = null

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
                render(newContext)
            }
        }
    }

    private fun render(context: PageContext): RenderResult {
        val renderPage = renderPage()
        val renderedPageContext = renderedPageContext
        val dirtyRect =
                if (renderedPageContext == null) {
                    renderPage.rect
                } else {
                    renderPage.dirtyRect(renderedPageContext, context)
                }
        this.renderedPageContext = context

        return if (dirtyRect.isEmpty) {
            RenderResult(null, dirtyRect)
        } else {
            val buffer = bitmapBufferPool.acquire()
            val canvas = buffer.canvas

            canvas.drawColor(Color.TRANSPARENT.value, PorterDuff.Mode.CLEAR)
            canvas.save()
            canvas.clipRect(dirtyRect.left, dirtyRect.top, dirtyRect.right, dirtyRect.bottom)
            renderPage.paint(canvas, context)
            canvas.restore()

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

    fun refresh() {
        bitmapLoader.completeIfReady { result ->
            if (result.buffer != null) {
                texture.refreshBy(result.buffer.bitmap, result.dirtyRect)
                refreshed = true
            }
        }
    }

    fun draw(matrix: FloatArray) {
        background.draw(matrix)

        if (refreshed)
            texturePlane.draw(matrix, texture)
    }

    private class RenderResult(val buffer: BitmapBuffer?, val dirtyRect: Rect)
}