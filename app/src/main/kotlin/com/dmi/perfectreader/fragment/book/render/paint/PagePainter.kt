package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage
import com.dmi.util.graphic.Rect

// todo задействовать dirtyRect
class PagePainter(private val objectPainter: UniversalObjectPainter) {
    fun dirtyRect(renderPage: RenderPage, oldContext: PageContext?, newContext: PageContext): Rect {
        val pageRange = renderPage.page.range
        if (newContext != oldContext) {
            if (oldContext == null) {
                return Rect(0, 0, 1, 1)
            } else {
                val oldPageSelection = if (oldContext.selectionRange != null) oldContext.selectionRange intersects pageRange else null
                val newPageSelection = if (newContext.selectionRange != null) newContext.selectionRange intersects pageRange else null
                if (oldPageSelection != newPageSelection) {
                    return Rect(0, 0, 1, 1)
                } else {
                    return Rect(0, 0, 0, 0)
                }
            }
        } else {
            return Rect(0, 0, 0, 0)
        }
    }

    fun paint(renderPage: RenderPage, context: PageContext, canvas: Canvas, dirtyRect: Rect) {
        val objects = renderPage.objects
        val margins = renderPage.page.margins
        val size = renderPage.page.size
        val contentSize = renderPage.page.contentSize

        canvas.save()
        canvas.clipRect(0F, margins.top, size.width, margins.top + contentSize.height)

        paintObjects(canvas, context, objects, PaintLayer.FRAME)
        paintObjects(canvas, context, objects, PaintLayer.IMAGE)
        paintObjects(canvas, context, objects, PaintLayer.SELECTION)
        paintObjects(canvas, context, objects, PaintLayer.TEXT)

        canvas.restore()
    }

    private fun paintObjects(canvas: Canvas, context: PageContext, objects: List<RenderObject>, layer: PaintLayer) {
        for (i in 0..objects.size - 1) {
            objectPainter.paint(objects[i], context, canvas, layer)
        }
    }
}