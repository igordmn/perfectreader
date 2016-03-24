package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.box.BoxLayouter
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.layout.frame.FrameLayouter
import com.dmi.perfectreader.layout.image.BitmapLoader
import com.dmi.perfectreader.layout.image.CachedLayouter
import com.dmi.perfectreader.layout.image.ImageLayouter
import com.dmi.perfectreader.layout.paragraph.ParagraphLayouter
import com.dmi.perfectreader.layout.paragraph.TextMetrics
import com.dmi.perfectreader.layout.paragraph.liner.Liner
import com.dmi.perfectreader.render.RenderObject

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
