package com.dmi.perfectreader.fragment.book.render.factory

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText

open class TextPainter {
    private val textPaintCache = PaintCache()

    fun paintTextShadow(x: Float, y: Float, obj: LayoutText, canvas: Canvas) {
        if (obj.style.textShadowEnabled && obj !is LayoutSpaceText) {
            val paint = textPaintCache.forShadow(obj.style)
            val shadowX = obj.style.shadowOffsetX
            val shadowY = obj.style.shadowOffsetY
            canvas.drawText(obj.text, 0, obj.text.length, x + shadowX, y + shadowY + obj.baseline, paint)
        }
    }

    fun paintText(x: Float, y: Float, obj: LayoutText, canvas: Canvas) {
        if (obj !is LayoutSpaceText) {
            val paint = textPaintCache.forText(obj.style)
            canvas.drawText(obj.text, 0, obj.text.length, x, y + obj.baseline, paint)
        }
    }

    private class PaintCache {
        private val paint = TextPaint()
        private val shadowPaint = TextPaint()

        private var lastBlurMaskFilter: BlurMaskFilter? = null
        private var lastBlurMaskFilterRadius: Float? = null
        private var lastStyle: ConfiguredFontStyle? = null

        fun forText(style: ConfiguredFontStyle): TextPaint {
            setStyle(style)
            return paint
        }

        fun forShadow(style: ConfiguredFontStyle): TextPaint {
            setStyle(style)
            return shadowPaint
        }

        private fun setStyle(style: ConfiguredFontStyle) {
            if (lastStyle !== style) {
                paint.textSize = style.size
                paint.textScaleX = style.scaleX
                paint.textSkewX = style.skewX
                paint.strokeWidth = style.strokeWidth
                paint.style = if (style.strokeWidth == 0F) Paint.Style.FILL else Paint.Style.FILL_AND_STROKE
                paint.color = style.color.value
                paint.isAntiAlias = style.antialiasing
                paint.isSubpixelText = style.subpixelPositioning
                paint.hinting = if (style.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
                paint.isLinearText = false

                if (style.textShadowEnabled) {
                    shadowPaint.textSize = style.size
                    shadowPaint.textScaleX = style.scaleX
                    shadowPaint.textSkewX = style.skewX
                    shadowPaint.strokeWidth = style.strokeWidth + style.shadowStrokeWidth
                    shadowPaint.style = if (style.strokeWidth + style.shadowStrokeWidth == 0F) Paint.Style.FILL else Paint.Style.FILL_AND_STROKE
                    shadowPaint.color = style.shadowColor.value
                    shadowPaint.isAntiAlias = style.antialiasing
                    shadowPaint.isSubpixelText = style.subpixelPositioning
                    shadowPaint.hinting = if (style.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
                    shadowPaint.isLinearText = false
                    shadowPaint.maskFilter = if (style.shadowBlurRadius > 0) blurMaskFilter(style.shadowBlurRadius) else null
                }

                lastStyle = style
            }
        }

        private fun blurMaskFilter(radius: Float): BlurMaskFilter {
            if (radius != lastBlurMaskFilterRadius) {
                lastBlurMaskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                lastBlurMaskFilterRadius = radius
            }
            return lastBlurMaskFilter!!
        }
    }
}