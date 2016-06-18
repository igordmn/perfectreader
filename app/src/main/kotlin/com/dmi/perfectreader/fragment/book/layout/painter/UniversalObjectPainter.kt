package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.obj.render.*
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder

class UniversalObjectPainter(bitmapDecoder: BitmapDecoder) : ObjectPainter<RenderObject> {
    private val framePainter = FramePainter()
    private val imagePainter = ImagePainter(bitmapDecoder)
    private val textPainter = TextPainter()

    override fun paintItself(obj: RenderObject, canvas: Canvas) = when (obj) {
        is RenderFrame -> framePainter.paintItself(obj, canvas)
        is RenderImage -> imagePainter.paintItself(obj, canvas)
        is RenderText -> textPainter.paintItself(obj, canvas)
        is RenderBox -> Unit
        is RenderLine -> Unit
        is RenderParagraph -> Unit
        else -> throw UnsupportedOperationException()
    }
}