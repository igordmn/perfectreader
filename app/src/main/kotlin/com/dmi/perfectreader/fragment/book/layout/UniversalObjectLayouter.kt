package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.paragraph.ParagraphLayouter
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.Liner
import com.dmi.perfectreader.fragment.book.layout.paragraph.metrics.TextMetrics
import com.dmi.perfectreader.fragment.book.content.obj.*
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject

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
        is ConfiguredImage -> imageLayouter.layout(obj, space)
        is ConfiguredFrame -> frameLayouter.layout(obj, space)
        is ConfiguredBox -> boxLayouter.layout(obj, space)
        else -> throw UnsupportedOperationException()
    }
}