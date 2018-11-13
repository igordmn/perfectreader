package com.dmi.perfectreader.ui.book.gl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.ui.book.render.factory.PageRenderer
import com.dmi.perfectreader.ui.book.render.obj.RenderObject
import com.dmi.perfectreader.ui.book.render.obj.RenderPage
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.Size
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private val pageDrawContext = newSingleThreadContext("pageDraw")

class GLPageRefresher(
        size: Size,
        private val pageRenderer: PageRenderer
) {
    private val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)

    // for protect bitmap from simultaneous access
    private val mutex = Mutex()

    suspend fun render(page: Page): RenderPage = withContext(pageDrawContext) {
        pageRenderer.render(page)
    }

    suspend fun refresh(
            page: RenderPage,
            texture: GLTexture,
            previousContext: RenderObject.Context?,
            currentContext: RenderObject.Context
    ) {
        mutex.withLock {
            val dirtyRect = withContext(pageDrawContext) {
                val dirtyRect = if (previousContext == null) {
                    page.rect
                } else {
                    page.dirtyRect(previousContext, currentContext)
                }
                if (dirtyRect != null)
                    page.paint(canvas, currentContext, dirtyRect)
                dirtyRect
            }
            if (dirtyRect != null)
                texture.refreshBy(bitmap, dirtyRect)
        }
    }

    private fun RenderPage.paint(canvas: Canvas, context: RenderObject.Context, dirtyRect: Rect) {
        canvas.save()
        canvas.clipRect(dirtyRect.left, dirtyRect.top, dirtyRect.right, dirtyRect.bottom)
        canvas.drawColor(Color.TRANSPARENT.value, PorterDuff.Mode.CLEAR)
        paint(canvas, context)
        canvas.restore()
    }
}