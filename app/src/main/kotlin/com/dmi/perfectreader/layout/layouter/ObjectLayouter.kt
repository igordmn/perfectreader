package com.dmi.perfectreader.layout.layouter

import com.dmi.perfectreader.layout.layouter.BoxLayouter
import com.dmi.perfectreader.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.layout.layouter.image.BitmapLoader
import com.dmi.perfectreader.layout.layouter.image.ImageLayouter
import com.dmi.perfectreader.layout.layouter.paragraph.ParagraphLayouter
import com.dmi.perfectreader.layout.layouter.paragraph.TextMetrics
import com.dmi.perfectreader.layout.layouter.paragraph.liner.Liner
import com.dmi.perfectreader.layout.layoutobj.*
import com.dmi.perfectreader.layout.renderobj.RenderObject

class ObjectLayouter(
        textMetrics: TextMetrics,
        liner: Liner,
        bitmapLoader: BitmapLoader
) : Layouter<LayoutObject, RenderObject> {
    private val childLayouter = CachedLayouter(this)

    private val paragraphLayouter = ParagraphLayouter(childLayouter, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapLoader)
    private val frameLayouter = FrameLayouter(childLayouter)
    private val boxLayouter = BoxLayouter(childLayouter)

    override fun layout(obj: LayoutObject, space: LayoutSpace): RenderObject {
        return when (obj) {
            is LayoutParagraph -> paragraphLayouter.layout(obj, space)
            is LayoutImage -> imageLayouter.layout(obj, space)
            is LayoutFrame -> frameLayouter.layout(obj, space)
            is LayoutBox -> boxLayouter.layout(obj, space)
            else -> throw UnsupportedOperationException()
        }
    }
}