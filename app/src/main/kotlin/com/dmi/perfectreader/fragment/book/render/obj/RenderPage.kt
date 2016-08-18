package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.graphic.Rect

class RenderPage(val layers: List<RenderObject>, val rect: Rect) : RenderObject() {
    // todo правильно вычислять dirty rect
    override fun dirtyRect(oldContext: PageContext, newContext: PageContext): Rect {
        if (isChanged(oldContext, newContext)) {
            return rect
        } else {
            return Rect.ZERO
        }
    }

    private fun isChanged(oldContext: PageContext, newContext: PageContext): Boolean {
        for (layer in layers) {
            if (!layer.dirtyRect(oldContext, newContext).isEmpty)
                return true
        }
        return false
    }

    override fun paint(canvas: Canvas, context: PageContext) {
        for (layer in layers) {
            layer.paint(canvas, context)
        }
    }
}