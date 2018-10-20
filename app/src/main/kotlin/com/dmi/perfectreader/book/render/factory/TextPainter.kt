package com.dmi.perfectreader.book.render.factory

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.text.TextPaint
import com.dmi.perfectreader.book.content.configure.common.ConfiguredFontStyle
import com.dmi.perfectreader.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.layout.paragraph.metrics.configureTextPaint
import com.dmi.perfectreader.book.layout.paragraph.metrics.configureTextShadowPaint
import kotlin.math.cos
import kotlin.math.sin

open class TextPainter {
    private val textPaintCache = PaintCache()

    fun paintTextShadow(x: Float, y: Float, obj: LayoutText, canvas: Canvas) {
        if (obj.style.shadowEnabled && obj !is LayoutSpaceText) {
            val paint = textPaintCache.forShadow(obj.style)
            val shadowX = obj.style.shadowOffset * cos(obj.style.shadowAngle)
            val shadowY = obj.style.shadowOffset * sin(obj.style.shadowAngle)
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
                configureTextPaint(paint, style)

                if (style.shadowEnabled) {
                    configureTextShadowPaint(shadowPaint, style)
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