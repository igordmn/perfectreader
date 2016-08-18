package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.graphic.Rect

abstract class RenderObject {
    abstract fun dirtyRect(oldContext: PageContext, newContext: PageContext): Rect
    abstract fun paint(canvas: Canvas, context: PageContext)
}