package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.layout.image.BitmapLoader
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
    private val paragraphLayouter = ParagraphLayouter(this, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapLoader)

    override fun layout(obj: LayoutObject, space: LayoutSpace): RenderObject {
        return when (obj) {
            is LayoutParagraph -> paragraphLayouter.layout(obj, space)
            is LayoutImage -> imageLayouter.layout(obj, space)
            else -> throw UnsupportedOperationException()
        }
    }
}
