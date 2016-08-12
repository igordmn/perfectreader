package com.dmi.perfectreader.fragment.book.render.paint

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.HINTING_OFF
import android.graphics.Paint.HINTING_ON
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutSpaceText
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
        val selectionRange = context.selectionRange
        if (selectionRange != null) {
            val selectionBeginIndex = beginIndexOfSelectedChar(layoutObj, selectionRange.begin)
            val selectionEndIndex = endIndexOfSelectedChar(layoutObj, selectionRange.end)

            if (selectionBeginIndex < selectionEndIndex) {
                selectionPaint.color = layoutObj.style.selectionConfig.backgroundColor.value
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