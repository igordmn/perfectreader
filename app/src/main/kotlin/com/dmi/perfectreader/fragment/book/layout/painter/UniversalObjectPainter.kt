package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.obj.layout.*

class UniversalObjectPainter(bitmapDecoder: BitmapDecoder) : ObjectPainter<LayoutObject> {
    private val framePainter = FramePainter()
    private val imagePainter = ImagePainter(bitmapDecoder)
    private val textPainter = TextPainter()

    override fun paintItself(obj: LayoutObject, canvas: Canvas, context: PaintContext) = when (obj) {
        is LayoutFrame -> framePainter.paintItself(obj, canvas, context)
        is LayoutImage -> imagePainter.paintItself(obj, canvas, context)
        is LayoutText -> textPainter.paintItself(obj, canvas, context)
        is LayoutBox -> Unit
        is LayoutLine -> Unit
        is LayoutParagraph -> Unit
        else -> throw UnsupportedOperationException()
    }
}