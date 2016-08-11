package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.obj.RenderFrame
import com.dmi.perfectreader.fragment.book.render.obj.RenderImage
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderText

class UniversalObjectPainter(
        private val framePainter: FramePainter,
        private val imagePainter: ImagePainter,
        private val textPainter: TextPainter
) {
    fun paint(obj: RenderObject, context: PageContext, canvas: Canvas, layer: PaintLayer) = when (obj) {
        is RenderFrame -> framePainter.paint(obj, canvas, layer)
        is RenderImage -> imagePainter.paint(obj, canvas, layer)
        is RenderText -> textPainter.paint(obj, context, canvas, layer)
        else -> throw UnsupportedOperationException()
    }
}