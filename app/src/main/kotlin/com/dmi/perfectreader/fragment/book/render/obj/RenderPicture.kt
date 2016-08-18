package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import android.graphics.Picture
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.graphic.Rect

class RenderPicture(private val picture: Picture) : RenderObject() {
    override fun dirtyRect(oldContext: PageContext, newContext: PageContext) = Rect.ZERO

    override fun paint(canvas: Canvas, context: PageContext) {
        canvas.drawPicture(picture)
    }
}