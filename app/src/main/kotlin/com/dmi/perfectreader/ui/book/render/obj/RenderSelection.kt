package com.dmi.perfectreader.ui.book.render.obj

import android.graphics.Canvas
import android.graphics.Paint
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.ui.book.selection.beginIndexOfSelectedChar
import com.dmi.perfectreader.ui.book.selection.endIndexOfSelectedChar
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.union
import com.dmi.util.lang.intCeil
import com.dmi.util.lang.intFloor

class RenderSelection(private val infoList: List<TextInfo>) : RenderObject() {
    private val selectionPaint = Paint()

    override fun dirtyRect(oldContext: Context, newContext: Context): Rect? {
        val oldSelection = oldContext.selection
        val newSelection = newContext.selection
        return if (oldSelection != null || newSelection != null) {
            var dirtyRect: Rect? = null
            for (info in infoList) {
                dirtyRect = dirtyRect union dirtyRect(info, oldSelection, newSelection)
            }
            dirtyRect
        } else {
            null
        }
    }

    private fun dirtyRect(info: TextInfo, oldSelection: LocationRange?, newSelection: LocationRange?): Rect? {
        val obj = info.obj

        val oldSelectionBeginIndex = if (oldSelection != null) beginIndexOfSelectedChar(obj, oldSelection.start) else 0
        val oldSelectionEndIndex = if (oldSelection != null) endIndexOfSelectedChar(obj, oldSelection.endInclusive) else 0
        val newSelectionBeginIndex = if (newSelection != null) beginIndexOfSelectedChar(obj, newSelection.start) else 0
        val newSelectionEndIndex = if (newSelection != null) endIndexOfSelectedChar(obj, newSelection.endInclusive) else 0

        val oldIsNotSelected = oldSelectionBeginIndex == oldSelectionEndIndex
        val newIsNotSelected = newSelectionBeginIndex == newSelectionEndIndex
        val newEqualsOld = oldSelectionBeginIndex == newSelectionBeginIndex && oldSelectionEndIndex == newSelectionEndIndex

        return if (oldIsNotSelected && newIsNotSelected || newEqualsOld) {
            null
        } else {
            Rect(intFloor(info.x), intFloor(info.y), intCeil(info.x + obj.width), intCeil(info.y + obj.height))
        }
    }

    override fun paint(canvas: Canvas, context: Context) {
        for (info in infoList) {
            paint(canvas, info, context)
        }
    }

    private fun paint(canvas: Canvas, info: TextInfo, context: Context) {
        val obj = info.obj
        val selection = context.selection
        if (selection != null) {
            val selectionBeginIndex = beginIndexOfSelectedChar(obj, selection.start)
            val selectionEndIndex = endIndexOfSelectedChar(obj, selection.endInclusive)

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