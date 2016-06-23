package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.HINTING_OFF
import android.graphics.Paint.HINTING_ON
import android.graphics.RectF
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import com.dmi.perfectreader.fragment.book.obj.render.RenderSpace
import com.dmi.perfectreader.fragment.book.obj.render.RenderText
import com.dmi.util.lang.intRound

open class TextPainter : ObjectPainter<RenderText> {
    private val textPaintCache = PaintCache()
    private val selectionBackgroundPaint = Paint()

    override fun paintItself(obj: RenderText, canvas: Canvas, context: PaintContext) {
        if (context.selectionRange != null) {
            val selectionBegin = obj.indexOf(context.selectionRange.begin)
            val selectionEnd = obj.indexOf(context.selectionRange.end)
            drawSelectionRect(obj, canvas, selectionBegin, selectionEnd)
            drawText(obj, canvas, 0, selectionBegin, false)
            drawText(obj, canvas, selectionBegin, selectionEnd, true)
            drawText(obj, canvas, selectionEnd, obj.text.length, false)
        } else {
            drawText(obj, canvas, 0, obj.text.length, false)
        }
    }

    private fun RenderText.indexOf(location: Location) = intRound(range.clampPercentOf(location) * text.length)

    private fun LocationRange.clampPercentOf(location: Location) = when {
        location >= begin && location <= end -> percentOf(location)
        location > end -> 1.0
        else -> 0.0
    }

    private fun drawText(obj: RenderText, canvas: Canvas, begin: Int, end: Int, selected: Boolean) {
        if (end > begin && obj !is RenderSpace) {
            val textPaint = textPaintCache.forStyle(obj.style, selected)
            val offsetX = obj.charOffsets[begin]
            canvas.drawText(obj.text, begin, end, offsetX, obj.baseline, textPaint)
        }
    }

    private fun drawSelectionRect(obj: RenderText, canvas: Canvas, begin: Int, end: Int) {
        val left = obj.charOffsets[begin]
        val right = if (end < obj.text.length) obj.charOffsets[end] else obj.width
        selectionBackgroundPaint.color = obj.style.selectionConfig.backgroundColor.value
        canvas.drawRect(
                RectF(left, 0F, right, obj.height),
                selectionBackgroundPaint
        )
    }

    private class PaintCache {
        private val paint = TextPaint()

        private var lastStyle: LayoutFontStyle? = null
        private var lastSelected: Boolean? = null

        fun forStyle(style: LayoutFontStyle, selected: Boolean): TextPaint {
            if (lastStyle !== style || lastSelected !== selected) {
                paint.isAntiAlias = style.renderConfig.antialias
                paint.isSubpixelText = style.renderConfig.subpixel
                paint.hinting = if (style.renderConfig.hinting) HINTING_ON else HINTING_OFF
                paint.isLinearText = style.renderConfig.linearScaling
                paint.color = if (selected) style.selectionConfig.textColor.value else style.color.value
                paint.textSize = style.size
                lastStyle = style
                lastSelected = selected
            }
            return paint
        }
    }
}