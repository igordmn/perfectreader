package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.HINTING_OFF
import android.graphics.Paint.HINTING_ON
import android.graphics.RectF
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.obj.RenderText
import com.dmi.util.lang.intRound

open class TextPainter {
    private val textPaintCache = PaintCache()
    private val selectionBackgroundPaint = Paint()

    fun paint(obj: RenderText, context: PageContext, canvas: Canvas, layer: PaintLayer) {
        when (layer) {
            PaintLayer.SELECTION -> paintSelection(obj, context, canvas)
            PaintLayer.TEXT -> paintText(canvas, obj)
            else -> Unit
        }
    }

    private fun paintText(canvas: Canvas, obj: RenderText) {
        val layoutObj = obj.layoutObj
        if (layoutObj !is LayoutSpaceText) {
            val paint = textPaintCache.forStyle(layoutObj.style)
            canvas.drawText(layoutObj.text, 0, layoutObj.text.length, obj.x, obj.y + layoutObj.baseline, paint)
        }
    }

    private fun paintSelection(obj: RenderText, context: PageContext, canvas: Canvas) {
        val layoutObj = obj.layoutObj
        if (context.selectionRange != null) {
            val selectionBegin = layoutObj.indexOf(context.selectionRange.begin)
            val selectionEnd = layoutObj.indexOf(context.selectionRange.end)
            drawSelectionRect(obj.x, obj.y, layoutObj, canvas, selectionBegin, selectionEnd)
        }
    }

    private fun LayoutText.indexOf(location: Location) = intRound(range.clampPercentOf(location) * text.length)

    private fun LocationRange.clampPercentOf(location: Location) = when {
        location >= begin && location <= end -> percentOf(location)
        location > end -> 1.0
        else -> 0.0
    }

    private fun drawSelectionRect(x: Float, y: Float, obj: LayoutText, canvas: Canvas, begin: Int, end: Int) {
        selectionBackgroundPaint.color = obj.style.selectionConfig.backgroundColor.value
        canvas.drawRect(
                RectF(x + obj.charOffset(begin), y, x + obj.charOffset(end), y + obj.height),
                selectionBackgroundPaint
        )
    }

    private class PaintCache {
        private val paint = TextPaint()

        private var lastStyle: ConfiguredFontStyle? = null

        fun forStyle(style: ConfiguredFontStyle): TextPaint {
            if (lastStyle !== style) {
                paint.isAntiAlias = style.renderConfig.antialias
                paint.isSubpixelText = style.renderConfig.subpixel
                paint.hinting = if (style.renderConfig.hinting) HINTING_ON else HINTING_OFF
                paint.isLinearText = style.renderConfig.linearScaling
                paint.color = style.color.value
                paint.textSize = style.size
                lastStyle = style
            }
            return paint
        }
    }
}