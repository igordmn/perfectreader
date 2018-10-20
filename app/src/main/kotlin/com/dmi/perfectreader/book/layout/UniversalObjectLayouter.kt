package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.content.configure.*
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.layout.paragraph.ParagraphLayouter
import com.dmi.perfectreader.book.layout.paragraph.liner.Liner
import com.dmi.perfectreader.book.layout.paragraph.metrics.TextMetrics

class UniversalObjectLayouter(
        textMetrics: TextMetrics,
        liner: Liner,
        bitmapDecoder: BitmapDecoder
) : ObjectLayouter<ConfiguredObject, LayoutObject> {
    private val childLayouter = CachedLayouter(this)

    private val paragraphLayouter = ParagraphLayouter(childLayouter, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapDecoder)
    private val frameLayouter = FrameLayouter(childLayouter)
    private val boxLayouter = BoxLayouter(childLayouter)

    override fun layout(obj: ConfiguredObject, space: LayoutSpace) = when (obj) {
        is ConfiguredParagraph -> paragraphLayouter.layout(obj, space)
        is ConfiguredBox -> boxLayouter.layout(obj, space)
        is ConfiguredFrame -> frameLayouter.layout(obj, space)
        is ConfiguredImage -> imageLayouter.layout(obj, space)
        else -> throw UnsupportedOperationException()
    }
}