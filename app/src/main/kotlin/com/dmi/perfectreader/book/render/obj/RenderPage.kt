package com.dmi.perfectreader.book.render.obj

import android.graphics.Canvas
import com.dmi.perfectreader.book.pagination.page.PageContext
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.union

class RenderPage(val layers: List<RenderObject>, val rect: Rect) : RenderObject() {
    override fun dirtyRect(oldContext: PageContext, newContext: PageContext): Rect? {
        var dirtyRect: Rect? = null
        for (layer in layers) {
            dirtyRect = dirtyRect union layer.dirtyRect(oldContext, newContext)
        }
        return dirtyRect
    }

    override fun paint(canvas: Canvas, context: PageContext) {
        for (layer in layers) {
            layer.paint(canvas, context)
        }
    }
}