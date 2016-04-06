package com.dmi.perfectreader.render

import android.graphics.Canvas
import android.graphics.Paint.HINTING_OFF
import android.graphics.Paint.HINTING_ON
import android.text.TextPaint
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.style.FontStyle
import java.util.*
import java.util.Collections.emptyList

open class RenderText(width: Float,
                      height: Float,
                      val text: CharSequence,
                      val locale: Locale,
                      val baseline: Float,
                      val style: FontStyle,
                      range: BookRange
) : RenderObject(width, height, emptyList<RenderChild>(), range) {
    override fun paintItself(canvas: Canvas) {
        super.paintItself(canvas)
        val paint = PaintCache.forStyle(style)
        canvas.drawText(text, 0, text.length, 0F, baseline, paint)
    }

    private object PaintCache {
        private val paint = TextPaint()

        private var lastStyle: FontStyle? = null

        fun forStyle(style: FontStyle): TextPaint {
            if (lastStyle !== style) {
                paint.isAntiAlias = style.renderParams.antialias
                paint.isSubpixelText = style.renderParams.subpixel
                paint.hinting = if (style.renderParams.hinting) HINTING_ON else HINTING_OFF
                paint.isLinearText = style.renderParams.linearScaling
                paint.color = style.color
                paint.textSize = style.size
                lastStyle = style
            }
            return paint
        }
    }
}
