package com.dmi.perfectreader.fragment.book.layout.paragraph.metrics

import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedFontStyle
import com.dmi.util.lang.Reusable
import com.dmi.util.lang.ReusableFloatArray

class PaintTextMetrics : TextMetrics {
    @Reusable
    override fun charWidths(text: CharSequence, style: ComputedFontStyle): FloatArray {
        val textPaint = Reusables.textPaint
        val charWidths = Reusables.charWidths(text.length)

        with (textPaint) {
            isAntiAlias = style.renderConfig.antialias
            isSubpixelText = style.renderConfig.subpixel
            hinting = if (style.renderConfig.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
            isLinearText = style.renderConfig.linearScaling
            textSize = style.size
            getTextWidths(text, 0, text.length, charWidths)
        }

        return charWidths
    }

    @Reusable
    override fun verticalMetrics(style: ComputedFontStyle): TextMetrics.VerticalMetrics {
        val textPaint = Reusables.textPaint
        val verticalMetrics = Reusables.verticalMetrics
        val paintFontMetrics = Reusables.paintFontMetrics

        with (textPaint) {
            textSize = style.size
            getFontMetrics(paintFontMetrics)
        }

        return verticalMetrics.apply {
            ascent = paintFontMetrics.ascent
            descent = paintFontMetrics.descent
            leading = paintFontMetrics.leading
        }
    }

    private object Reusables {
        val textPaint = TextPaint()
        val paintFontMetrics = Paint.FontMetrics()
        val verticalMetrics = TextMetrics.VerticalMetrics()
        val charWidths = ReusableFloatArray()
    }
}