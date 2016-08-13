package com.dmi.perfectreader.fragment.book.layout.paragraph.metrics

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.util.lang.Reusable

interface TextMetrics {
    @Reusable
    fun charAdvances(text: CharSequence, style: ConfiguredFontStyle): FloatArray

    fun verticalMetrics(style: ConfiguredFontStyle): VerticalMetrics

    @Reusable
    class VerticalMetrics {
        var ascent = 0F
        var descent = 0F
        var leading = 0F
    }
}