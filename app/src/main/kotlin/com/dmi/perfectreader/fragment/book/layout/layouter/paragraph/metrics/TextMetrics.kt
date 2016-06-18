package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.metrics

import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import com.dmi.util.lang.Reusable

interface TextMetrics {
    @Reusable
    fun charWidths(text: CharSequence, style: LayoutFontStyle): FloatArray

    fun verticalMetrics(style: LayoutFontStyle): VerticalMetrics

    @Reusable
    class VerticalMetrics {
        var ascent = 0F
        var descent = 0F
        var leading = 0F
    }
}