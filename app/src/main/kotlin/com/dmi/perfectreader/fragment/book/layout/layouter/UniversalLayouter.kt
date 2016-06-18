package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.image.ImageLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.ParagraphLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.liner.Liner
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.metrics.TextMetrics
import com.dmi.perfectreader.fragment.book.obj.layout.*
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder

class UniversalLayouter(
        textMetrics: TextMetrics,
        liner: Liner,
        bitmapDecoder: BitmapDecoder
) : Layouter<LayoutObject, RenderObject> {
    private val childLayouter = CachedLayouter(this)

    private val paragraphLayouter = ParagraphLayouter(childLayouter, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapDecoder)
    private val frameLayouter = FrameLayouter(childLayouter)
    private val boxLayouter = BoxLayouter(childLayouter)

    override fun layout(obj: LayoutObject, space: LayoutSpace) = when (obj) {
        is LayoutParagraph -> paragraphLayouter.layout(obj, space)
        is LayoutImage -> imageLayouter.layout(obj, space)
        is LayoutFrame -> frameLayouter.layout(obj, space)
        is LayoutBox -> boxLayouter.layout(obj, space)
        else -> throw UnsupportedOperationException()
    }
}