package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.config.LayoutContext
import com.dmi.perfectreader.layout.layouter.BitmapLoader
import com.dmi.perfectreader.layout.layouter.ImageLayouter
import com.dmi.perfectreader.layout.layouter.Layouter
import com.dmi.perfectreader.layout.layouter.ParagraphLayouter
import com.dmi.perfectreader.layout.liner.Liner
import com.dmi.perfectreader.layout.paragraph.TextMetrics
import com.dmi.perfectreader.render.RenderObject

class ObjectLayouter(
        textMetrics: TextMetrics,
        liner: Liner,
        bitmapLoader: BitmapLoader
) : Layouter<LayoutObject, RenderObject> {
    private val paragraphLayouter = ParagraphLayouter(this, textMetrics, liner)
    private val imageLayouter = ImageLayouter(bitmapLoader)

    override fun layout(obj: LayoutObject, context: LayoutContext): RenderObject {
        return when (obj) {
            is LayoutParagraph -> paragraphLayouter.layout(obj, context)
            is LayoutImage -> imageLayouter.layout(obj, context)
            else -> throw UnsupportedOperationException()
        }
    }
}
