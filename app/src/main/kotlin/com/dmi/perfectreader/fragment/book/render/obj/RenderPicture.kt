package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import android.graphics.Picture
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext

class RenderPicture(private val picture: Picture) : RenderObject() {
    override fun dirtyRect(oldContext: PageContext, newContext: PageContext) = null

    override fun paint(canvas: Canvas, context: PageContext) {
        canvas.drawPicture(picture)
    }
}