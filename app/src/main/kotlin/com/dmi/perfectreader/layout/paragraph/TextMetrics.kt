package com.dmi.perfectreader.layout.paragraph

import com.dmi.perfectreader.style.FontStyle
import com.dmi.util.annotation.Reusable

interface TextMetrics {
    @Reusable
    fun charWidths(text: CharSequence, style: FontStyle): FloatArray
    fun verticalMetrics(style: FontStyle): VerticalMetrics

    @Reusable
    class VerticalMetrics {
        var ascent = 0F
        var descent = 0F
        var leading = 0F
    }
}
