package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.selection.beginIndexOfSelectedChar
import com.dmi.perfectreader.fragment.book.selection.endIndexOfSelectedChar
import com.dmi.util.graphic.Rect

class RenderSelection(private val infoList: List<TextInfo>) : RenderObject() {
    private val selectionPaint = Paint()

    // todo правильно вычислять dirty rect
    override fun dirtyRect(oldContext: PageContext, newContext: PageContext): Rect {
        for (info in infoList) {
            if (isChanged(info.obj, oldContext, newContext))
                return Rect(0, 0, 10000, 10000)
        }
        return Rect.ZERO
    }

    private fun isChanged(obj: LayoutText, oldContext: PageContext, newContext: PageContext): Boolean {
        val oldSelectionRange = oldContext.selectionRange
        val newSelectionRange = newContext.selectionRange
        return when {
            oldSelectionRange != null && newSelectionRange != null -> isChanged(obj, oldSelectionRange, newSelectionRange)
            oldSelectionRange == null && newSelectionRange == null -> false
            else -> true
        }
    }

    private fun isChanged(obj: LayoutText, oldSelectionRange: LocationRange, newSelectionRange: LocationRange): Boolean {
        val oldSelectionBeginIndex = beginIndexOfSelectedChar(obj, oldSelectionRange.begin)
        val oldSelectionEndIndex = endIndexOfSelectedChar(obj, oldSelectionRange.end)
        val newSelectionBeginIndex = beginIndexOfSelectedChar(obj, newSelectionRange.begin)
        val newSelectionEndIndex = endIndexOfSelectedChar(obj, newSelectionRange.end)
        return newSelectionBeginIndex != oldSelectionBeginIndex || newSelectionEndIndex != oldSelectionEndIndex
    }

    override fun paint(canvas: Canvas, context: PageContext) {
        for (info in infoList) {
            paint(canvas, info, context)
        }
    }

    private fun paint(canvas: Canvas, info: TextInfo, context: PageContext) {
        val obj = info.obj
        val selectionRange = context.selectionRange
        if (selectionRange != null) {
            val selectionBeginIndex = beginIndexOfSelectedChar(obj, selectionRange.begin)
            val selectionEndIndex = endIndexOfSelectedChar(obj, selectionRange.end)

            if (selectionBeginIndex < selectionEndIndex) {
                selectionPaint.color = obj.style.selectionColor.value
                val selectionLeft = obj.charOffset(selectionBeginIndex)
                val selectionRight = obj.charOffset(selectionEndIndex)
                canvas.drawRect(
                        info.x + selectionLeft, info.y, info.x + selectionRight, info.y + obj.height,
                        selectionPaint
                )
            }
        }
    }

    class TextInfo(val x: Float, val y: Float, val obj: LayoutText)
}