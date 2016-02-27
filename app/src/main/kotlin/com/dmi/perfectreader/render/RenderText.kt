package com.dmi.perfectreader.render

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.style.FontStyle
import java.util.*
import java.util.Collections.emptyList

open class RenderText(width: Float, height: Float, private val text: CharSequence, private val locale: Locale, private val baseline: Float, private val style: FontStyle) : RenderObject(width, height, emptyList<RenderChild>()) {

    fun text(): CharSequence {
        return text
    }

    fun locale(): Locale {
        return locale
    }

    fun baseline(): Float {
        return baseline
    }

    fun style(): FontStyle {
        return style
    }

    override fun canPartiallyPainted(): Boolean {
        return false
    }

    override fun paintItself(canvas: Canvas) {
        super.paintItself(canvas)
        val paint = PaintCache.forStyle(style)
        canvas.drawText(text, 0, text.length, 0f, baseline, paint)
    }

    private object PaintCache {
        private val paint = TextPaint()

        private var lastStyle: FontStyle? = null

        fun forStyle(style: FontStyle): TextPaint {
            if (lastStyle !== style) {
                paint.isAntiAlias = style.renderParams().textAntialias()
                paint.isSubpixelText = style.renderParams().textSubpixel()
                paint.hinting = if (style.renderParams().textHinting()) Paint.HINTING_ON else Paint.HINTING_OFF
                paint.isLinearText = style.renderParams().textLinearScaling()
                paint.color = style.color()
                paint.textSize = style.size()
                lastStyle = style
            }
            return paint
        }
    }
}
