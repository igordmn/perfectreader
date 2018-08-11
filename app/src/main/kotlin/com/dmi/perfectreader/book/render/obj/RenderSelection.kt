package com.dmi.perfectreader.book.render.obj

import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.pagination.page.PageContext
import com.dmi.perfectreader.book.selection.beginIndexOfSelectedChar
import com.dmi.perfectreader.book.selection.endIndexOfSelectedChar
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.union
import com.dmi.util.lang.intCeil
import com.dmi.util.lang.intFloor

class RenderSelection(private val infoList: List<TextInfo>) : RenderObject() {
    private val selectionPaint = Paint()

    override fun dirtyRect(oldContext: PageContext, newContext: PageContext): Rect? {
        val oldSelectionRange = oldContext.selectionRange
        val newSelectionRange = newContext.selectionRange
        if (oldSelectionRange != null || newSelectionRange != null) {
            var dirtyRect: Rect? = null
            for (info in infoList) {
                dirtyRect = dirtyRect union dirtyRect(info, oldSelectionRange, newSelectionRange)
            }
            return dirtyRect
        } else {
            return null
        }
    }

    private fun dirtyRect(info: TextInfo, oldSelectionRange: LocationRange?, newSelectionRange: LocationRange?): Rect? {
        val obj = info.obj

        val oldSelectionBeginIndex = if (oldSelectionRange != null) beginIndexOfSelectedChar(obj, oldSelectionRange.begin) else 0
        val oldSelectionEndIndex = if (oldSelectionRange != null) endIndexOfSelectedChar(obj, oldSelectionRange.end) else 0
        val newSelectionBeginIndex = if (newSelectionRange != null) beginIndexOfSelectedChar(obj, newSelectionRange.begin) else 0
        val newSelectionEndIndex = if (newSelectionRange != null) endIndexOfSelectedChar(obj, newSelectionRange.end) else 0

        val oldIsNotSelected = oldSelectionBeginIndex == oldSelectionEndIndex
        val newIsNotSelected = newSelectionBeginIndex == newSelectionEndIndex
        val newEqualsOld = oldSelectionBeginIndex == newSelectionBeginIndex && oldSelectionEndIndex == newSelectionEndIndex

        return if (oldIsNotSelected && newIsNotSelected || newEqualsOld) {
            null
        } else {
            Rect(intFloor(info.x), intFloor(info.y), intCeil(info.x + obj.width), intCeil(info.y + obj.height))
        }
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