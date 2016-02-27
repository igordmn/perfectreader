package com.dmi.perfectreader.layout.config

import com.dmi.perfectreader.style.FontStyle
import com.dmi.util.annotation.Reusable

interface TextMetrics {
    @Reusable
    fun charWidths(text: CharSequence, style: FontStyle): FloatArray

    fun verticalMetrics(style: FontStyle): VerticalMetrics

    @Reusable
    open class VerticalMetrics {
        var ascent: Float = 0.toFloat()
        var descent: Float = 0.toFloat()
        var leading: Float = 0.toFloat()

        fun ascent(): Float {
            return ascent
        }

        fun descent(): Float {
            return descent
        }

        fun leading(): Float {
            return leading
        }
    }
}
