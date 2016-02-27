package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.config.LayoutArea
import com.dmi.perfectreader.layout.config.TextMetrics
import com.dmi.perfectreader.layout.layouter.Layouter
import com.dmi.perfectreader.layout.layouter.ParagraphLayouter
import com.dmi.perfectreader.layout.liner.Liner
import com.dmi.perfectreader.render.RenderObject

class ObjectLayouter(textMetrics: TextMetrics, liner: Liner) : Layouter<LayoutObject, RenderObject> {
    private val paragraphLayouter: ParagraphLayouter

    init {
        this.paragraphLayouter = ParagraphLayouter(this, textMetrics, liner)
    }

    override fun layout(`object`: LayoutObject, area: LayoutArea): RenderObject {
        if (`object` is LayoutParagraph) {
            return paragraphLayouter.layout(`object`, area)
        } else {
            throw UnsupportedOperationException()
        }
    }
}
