package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import android.graphics.Paint.HINTING_OFF
import android.graphics.Paint.HINTING_ON
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import com.dmi.perfectreader.fragment.book.obj.render.RenderText

open class TextPainter : ObjectPainter<RenderText> {
    private val paintCache = PaintCache()

    override fun paintItself(obj: RenderText, canvas: Canvas) {
        with (obj) {
            val paint = paintCache.forStyle(style)
            canvas.drawText(text, 0, text.length, 0F, baseline, paint)
        }
    }

    private class PaintCache {
        private val paint = TextPaint()

        private var lastStyle: LayoutFontStyle? = null

        fun forStyle(style: LayoutFontStyle): TextPaint {
            if (lastStyle !== style) {
                paint.isAntiAlias = style.renderParams.antialias
                paint.isSubpixelText = style.renderParams.subpixel
                paint.hinting = if (style.renderParams.hinting) HINTING_ON else HINTING_OFF
                paint.isLinearText = style.renderParams.linearScaling
                paint.color = style.color.value
                paint.textSize = style.size
                lastStyle = style
            }
            return paint
        }
    }
}