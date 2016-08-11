package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.location.LocationRange
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
                val oldContainsSelection = oldContext.selectionRange != null && selectionIntersects(pageRange, oldContext.selectionRange)
                val newContainsSelection = newContext.selectionRange != null && selectionIntersects(pageRange, newContext.selectionRange)
                if (newContainsSelection || oldContainsSelection) {
                    return Rect(0, 0, 1, 1)
                } else {
                    return Rect(0, 0, 0, 0)
                }
            }
        } else {
            return Rect(0, 0, 0, 0)
        }
    }

    private fun selectionIntersects(pageRange: LocationRange, selectionRange: LocationRange): Boolean {
        return selectionRange.begin >= pageRange.begin && selectionRange.begin < pageRange.end ||
               selectionRange.end > pageRange.begin && selectionRange.end <= pageRange.end
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