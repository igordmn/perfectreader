package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.image.ImageLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.ParagraphLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.liner.Liner
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.metrics.TextMetrics
import com.dmi.perfectreader.fragment.book.obj.layout.*
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.obj.content.*

class UniversalLayouter(
        textMetrics: TextMetrics,
        liner: Liner,
        bitmapDecoder: BitmapDecoder
) : Layouter<ComputedObject, LayoutObject> {
    private val childLayouter = CachedLayouter(this)

    private val paragraphLayouter = ParagraphLayouter(childLayouter, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapDecoder)
    private val frameLayouter = FrameLayouter(childLayouter)
    private val boxLayouter = BoxLayouter(childLayouter)

    override fun layout(obj: ComputedObject, space: LayoutSpace) = when (obj) {
        is ComputedParagraph -> paragraphLayouter.layout(obj, space)
        is ComputedImage -> imageLayouter.layout(obj, space)
        is ComputedFrame -> frameLayouter.layout(obj, space)
        is ComputedBox -> boxLayouter.layout(obj, space)
        else -> throw UnsupportedOperationException()
    }
}