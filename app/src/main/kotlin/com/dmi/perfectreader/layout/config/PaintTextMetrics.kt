package com.dmi.perfectreader.layout.config

import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.style.FontStyle
import com.dmi.util.annotation.Reusable
import com.dmi.util.cache.ReusableFloatArray

class PaintTextMetrics : TextMetrics {
    @Reusable
    override fun charWidths(text: CharSequence, style: FontStyle): FloatArray {
        val textPaint = Reusables.textPaint
        val charWidths = Reusables.charWidths(text.length)

        textPaint.isAntiAlias = style.renderParams.antialias
        textPaint.isSubpixelText = style.renderParams.subpixel
        textPaint.hinting = if (style.renderParams.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
        textPaint.isLinearText = style.renderParams.linearScaling
        textPaint.textSize = style.size
        textPaint.getTextWidths(text, 0, text.length, charWidths)

        return charWidths
    }

    @Reusable
    override fun verticalMetrics(style: FontStyle): TextMetrics.VerticalMetrics {
        val textPaint = Reusables.textPaint
        val verticalMetrics = Reusables.verticalMetrics
        val paintFontMetrics = Reusables.paintFontMetrics

        textPaint.textSize = style.size
        textPaint.getFontMetrics(paintFontMetrics)
        verticalMetrics.ascent = paintFontMetrics.ascent
        verticalMetrics.descent = paintFontMetrics.descent
        verticalMetrics.leading = paintFontMetrics.leading

        return verticalMetrics
    }

    private object Reusables {
        val textPaint = TextPaint()
        val paintFontMetrics = Paint.FontMetrics()
        val verticalMetrics = TextMetrics.VerticalMetrics()
        val charWidths = ReusableFloatArray()
    }
}
