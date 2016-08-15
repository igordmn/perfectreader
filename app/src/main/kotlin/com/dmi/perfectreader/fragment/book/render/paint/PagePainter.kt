package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage
import com.dmi.util.graphic.Rect

// todo задействовать dirtyRect
class PagePainter(private val objectPainter: UniversalObjectPainter) {
    fun dirtyRect(renderPage: RenderPage, oldContext: PageContext?, newContext: PageContext): Rect {
        if (isChangedOrNew(renderPage, oldContext, newContext)) {
            return Rect(0, 0, 1, 1)
        } else {
            return Rect(0, 0, 0, 0)
        }
    }

    private fun isChangedOrNew(renderPage: RenderPage, oldContext: PageContext?, newContext: PageContext): Boolean {
        return oldContext == null || isChanged(renderPage, oldContext, newContext)
    }

    private fun isChanged(renderPage: RenderPage, oldContext: PageContext, newContext: PageContext): Boolean {
        val objects = renderPage.objects
        for (i in 0..objects.size - 1) {
            if (objectPainter.isChanged(objects[i], oldContext, newContext))
                return true
        }
        return false
    }

    fun paint(renderPage: RenderPage, context: PageContext, canvas: Canvas, dirtyRect: Rect) {
        paintObjects(canvas, context, renderPage.objects, PaintLayer.FRAME)
        paintObjects(canvas, context, renderPage.objects, PaintLayer.IMAGE)
        paintObjects(canvas, context, renderPage.objects, PaintLayer.SELECTION)
        paintObjects(canvas, context, renderPage.objects, PaintLayer.TEXT)
    }

    private fun paintObjects(canvas: Canvas, context: PageContext, objects: List<RenderObject>, layer: PaintLayer) {
        for (i in 0..objects.size - 1) {
            objectPainter.paint(objects[i], context, canvas, layer)
        }
    }
}