package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.obj.*
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext

class UniversalObjectPainter(bitmapDecoder: BitmapDecoder) : ObjectPainter<LayoutObject> {
    private val framePainter = FramePainter()
    private val imagePainter = ImagePainter(bitmapDecoder)
    private val textPainter = TextPainter()

    override fun paintItself(obj: LayoutObject, context: PageContext, canvas: Canvas) = when (obj) {
        is LayoutFrame -> framePainter.paintItself(obj, context, canvas)
        is LayoutImage -> imagePainter.paintItself(obj, context, canvas)
        is LayoutText -> textPainter.paintItself(obj, context, canvas)
        is LayoutBox -> Unit
        is LayoutLine -> Unit
        is LayoutParagraph -> Unit
        else -> throw UnsupportedOperationException()
    }
}