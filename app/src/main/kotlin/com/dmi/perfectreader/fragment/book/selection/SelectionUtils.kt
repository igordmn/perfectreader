package com.dmi.perfectreader.fragment.book.selection

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.sqrDistanceToRect

/**
 * Поиск каретки с ближайшей нижней половиной по y, либо по x (если y одинаков)
 * @param oppositeLocation сюда передается selectionRange.begin, если ищется selectionRange.end, и наоборот
 */
fun selectionCaretNearestTo(page: Page, x: Float, y: Float, oppositeLocation: Location): Caret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    iterateSelectableCharIndices(page) { objLeft, objTop, obj, i ->
        val oppositeCharIndex = when {
            oppositeLocation < obj.range.begin -> 0
            oppositeLocation > obj.range.end -> obj.charCount
            else -> obj.charIndex(oppositeLocation)
        }
        if (i < oppositeCharIndex && i < obj.charCount || i > oppositeCharIndex && i > 0) {
            val caretX = objLeft + obj.charOffset(i)
            val caretHalf = objTop + obj.height / 2
            val caretBottom = objTop + obj.height
            val xDistance = Math.abs(x - caretX)
            val yDistance = when {
                y < caretHalf -> caretHalf - y
                y > caretBottom -> y - caretBottom
                else -> 0F
            }
            if (yDistance < nearestYDistance || yDistance == nearestYDistance && xDistance <= nearestXDistance) {
                nearestObj = obj
                nearestCharIndex = i
                nearestXDistance = xDistance
                nearestYDistance = yDistance
            }
        }
    }
    return if (nearestObj != null) Caret(nearestObj!!, nearestCharIndex) else null
}

fun selectionCaretAt(page: Page, location: Location, isLeft: Boolean): Caret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1

    iterateSelectableCharIndices(page) { objLeft, objTop, obj, i ->
        val charLocation = obj.charLocation(i)
        if (isLeft && i < obj.charCount && nearestObj == null && charLocation >= location ||
            !isLeft && i > 0 && charLocation <= location
        ) {
            nearestObj = obj
            nearestCharIndex = i
        }
    }

    return if (nearestObj != null) Caret(nearestObj!!, nearestCharIndex) else null
}

private inline fun iterateSelectableCharIndices(
        page: Page,
        crossinline action: (objLeft: Float, objTop: Float, obj: LayoutText, i: Int) -> Unit
) {
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && isSelectable(obj)) {
            for (i in 0..obj.charCount) {
                action(objLeft, objTop, obj, i)
            }
        }
    }
}

data class Caret(val obj: LayoutText, val charIndex: Int)

// порядок проверок составлен таким образом, чтобы исключить переносы на краю выделения
fun beginIndexOfSelectedChar(layoutText: LayoutText, selectionBegin: Location) = when {
    selectionBegin >= layoutText.range.end -> layoutText.charCount
    selectionBegin <= layoutText.range.begin -> 0
    else -> layoutText.charIndex(selectionBegin)
}

// порядок проверок составлен таким образом, чтобы исключить переносы на краю выделения
fun endIndexOfSelectedChar(layoutText: LayoutText, selectionEnd: Location) = when {
    selectionEnd <= layoutText.range.begin -> 0
    selectionEnd >= layoutText.range.end -> layoutText.charCount
    else -> layoutText.charIndex(selectionEnd)
}

/**
 * Исключает объекты, которые были добавлены в процессе форматирования текста, вроде переносов.
 * Выделять переносы можно только, если они находятся не на краю выделения, а между двух выделенных символов
 */
fun isSelectable(layoutText: LayoutText) = layoutText.range.end > layoutText.range.begin

fun clickableObjectAt(page: Page, x: Float, y: Float, radius: Float): LayoutObject? {
    var nearestObj: LayoutObject? = null
    var nearestSqrDistance = Float.MAX_VALUE
    val sqrRadius = radius * radius
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj.isClickable()) {
            val objRight = objLeft + obj.width
            val objBottom = objTop + obj.height

            val canBeNear = x >= objLeft - radius && y >= objTop - radius && x <= objRight + radius && y <= objBottom + radius
            if (canBeNear) {
                val sqrDistance = sqrDistanceToRect(x, y, objLeft, objTop, objRight, objBottom)
                if (sqrDistance <= sqrRadius && sqrDistance <= nearestSqrDistance) {
                    nearestObj = obj
                    nearestSqrDistance = sqrDistance
                }
            }
        }
    }
    return nearestObj
}

fun positionOf(page: Page, caret: Caret) = positionOf(page, caret.obj) + PositionF(caret.obj.charOffset(caret.charIndex), caret.obj.height)

fun positionOf(page: Page, obj: LayoutObject): PositionF {
    var objLeft = 0F
    var objTop = 0F
    var found = false
    page.forEachChildRecursive(0F, 0F) { itObjLeft, itObjTop, itObj ->
        if (itObj == obj) {
            objLeft = itObjLeft
            objTop = itObjTop
            found = true
        }
    }
    return if (found) PositionF(objLeft, objTop) else throw IllegalArgumentException()
}