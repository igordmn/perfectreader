package com.dmi.perfectreader.fragment.book.layout.paragraph.metrics

import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.util.lang.Reusable
import com.dmi.util.lang.ReusableFloatArray

class PaintTextMetrics : TextMetrics {
    @Reusable
    override fun charAdvances(text: CharSequence, style: ConfiguredFontStyle): FloatArray {
        val paint = Reusables.paint
        configureTextPaint(paint, style)

        val charWidths = Reusables.charWidths(text.length)
        paint.getTextWidths(text, 0, text.length, charWidths)
        return charWidths
    }

    @Reusable
    override fun verticalMetrics(style: ConfiguredFontStyle): TextMetrics.VerticalMetrics {
        val textPaint = Reusables.paint
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
        val paint = TextPaint()
        val paintFontMetrics = Paint.FontMetrics()
        val verticalMetrics = TextMetrics.VerticalMetrics()
        val charWidths = ReusableFloatArray()
    }
}