package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.obj.RenderText
import com.dmi.perfectreader.fragment.book.selection.beginIndexOfSelectedChar
import com.dmi.perfectreader.fragment.book.selection.endIndexOfSelectedChar

open class TextPainter {
    private val textPaintCache = PaintCache()
    private val selectionPaint = Paint()

    fun paint(obj: RenderText, context: PageContext, canvas: Canvas, layer: PaintLayer) {
        when (layer) {
            PaintLayer.SELECTION -> paintSelection(obj, context, canvas)
            PaintLayer.TEXT_SHADOW -> paintTextShadow(canvas, obj)
            PaintLayer.TEXT -> paintText(canvas, obj)
            else -> Unit
        }
    }

    fun isChanged(obj: RenderText, oldContext: PageContext, newContext: PageContext): Boolean {
        val oldSelectionRange = oldContext.selectionRange
        val newSelectionRange = newContext.selectionRange
        return when {
            oldSelectionRange != null && newSelectionRange != null -> isChanged(obj, oldSelectionRange, newSelectionRange)
            oldSelectionRange == null && newSelectionRange == null -> false
            else -> true
        }
    }

    fun isChanged(obj: RenderText, oldSelectionRange: LocationRange, newSelectionRange: LocationRange): Boolean {
        val oldSelectionBeginIndex = beginIndexOfSelectedChar(obj.layoutObj, oldSelectionRange.begin)
        val oldSelectionEndIndex = endIndexOfSelectedChar(obj.layoutObj, oldSelectionRange.end)
        val newSelectionBeginIndex = beginIndexOfSelectedChar(obj.layoutObj, newSelectionRange.begin)
        val newSelectionEndIndex = endIndexOfSelectedChar(obj.layoutObj, newSelectionRange.end)
        return newSelectionBeginIndex != oldSelectionBeginIndex || newSelectionEndIndex != oldSelectionEndIndex
    }

    private fun paintTextShadow(canvas: Canvas, obj: RenderText) {
        val layoutObj = obj.layoutObj
        if (layoutObj.style.textShadowEnabled && layoutObj !is LayoutSpaceText) {
            val paint = textPaintCache.forShadow(layoutObj.style)
            val shadowX = layoutObj.style.shadowOffsetX
            val shadowY = layoutObj.style.shadowOffsetY
            canvas.drawText(layoutObj.text, 0, layoutObj.text.length, obj.x + shadowX, obj.y + shadowY + layoutObj.baseline, paint)
        }
    }

    private fun paintText(canvas: Canvas, obj: RenderText) {
        val layoutObj = obj.layoutObj
        if (layoutObj !is LayoutSpaceText) {
            val paint = textPaintCache.forText(layoutObj.style)
            canvas.drawText(layoutObj.text, 0, layoutObj.text.length, obj.x, obj.y + layoutObj.baseline, paint)
        }
    }

    private fun paintSelection(obj: RenderText, context: PageContext, canvas: Canvas) {
        val layoutObj = obj.layoutObj
        val selectionRange = context.selectionRange
        if (selectionRange != null) {
            val selectionBeginIndex = beginIndexOfSelectedChar(layoutObj, selectionRange.begin)
            val selectionEndIndex = endIndexOfSelectedChar(layoutObj, selectionRange.end)

            if (selectionBeginIndex < selectionEndIndex) {
                selectionPaint.color = layoutObj.style.selectionColor.value
                val selectionLeft = layoutObj.charOffset(selectionBeginIndex)
                val selectionRight = layoutObj.charOffset(selectionEndIndex)
                canvas.drawRect(
                        obj.x + selectionLeft, obj.y, obj.x + selectionRight, obj.y + layoutObj.height,
                        selectionPaint
                )
            }
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
                    shadowPaint.style = if (style.strokeWidth == 0F) Paint.Style.FILL else Paint.Style.FILL_AND_STROKE
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