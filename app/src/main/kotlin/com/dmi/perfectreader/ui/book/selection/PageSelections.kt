package com.dmi.perfectreader.ui.book.selection

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.sqrDistanceToRect

fun Page.selectionCaretOfCharNearestTo(x: Float, y: Float): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    iterateSelectableObjects { objLeft, objTop, obj ->
        for (i in 0 until obj.charCount) {
            val charLeft = objLeft + obj.charOffset(i)
            val charRight = objLeft + obj.charOffset(i + 1)
            val charTop = objTop
            val charBottom = objTop + obj.height
            val xDistance = when {
                x < charLeft -> charLeft - x
                x > charRight -> x - charRight
                else -> 0F
            }
            val yDistance = when {
                y < charTop -> charTop - y
                y > charBottom -> y - charBottom
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
    return if (nearestObj != null) LayoutCaret(nearestObj!!, nearestCharIndex) else null
}

/**
 * Поиск каретки с ближайшей нижней половиной по y, либо по x (если y одинаков)
 * @param oppositeLocation сюда передается selectionRange.begin, если ищется selectionRange.end, и наоборот
 */
fun Page.selectionCaretNearestTo(x: Float, y: Float, oppositeLocation: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    iterateSelectableObjects { objLeft, objTop, obj ->
        for (i in 0..obj.charCount) {
            val oppositeCharIndex = when {
                oppositeLocation < obj.range.start -> 0
                oppositeLocation > obj.range.endInclusive -> obj.charCount
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
    }
    return if (nearestObj != null) LayoutCaret(nearestObj!!, nearestCharIndex) else null
}

fun Page.selectionCaretAtLeft(location: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var lastObj: LayoutText? = null

    iterateSelectableObjects { _, _, obj ->
        if (nearestObj == null) {
            when {
                location < obj.range.start -> {
                    nearestObj = obj
                    nearestCharIndex = 0
                }
                obj.range.endInclusive < location -> Unit
                else -> {
                    val index = obj.charIndex(location)
                    if (index < obj.charCount) {
                        nearestObj = obj
                        nearestCharIndex = index
                    }
                }
            }
        }
        lastObj = obj
    }

    if (nearestObj == null && lastObj != null) {
        nearestObj = lastObj
        nearestCharIndex = lastObj!!.charCount - 1
    }

    return if (nearestObj != null) LayoutCaret(nearestObj!!, nearestCharIndex) else null
}

fun Page.selectionCaretAtRight(location: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var firstObj: LayoutText? = null

    iterateSelectableObjects { _, _, obj ->
        if (firstObj == null)
            firstObj = obj

        when {
            location < obj.range.start -> Unit
            obj.range.endInclusive < location -> {
                nearestObj = obj
                nearestCharIndex = obj.charCount
            }
            else -> {
                val index = obj.charIndex(location)
                if (index > 0) {
                    nearestObj = obj
                    nearestCharIndex = index
                }
            }
        }
    }

    if (nearestObj == null && firstObj != null) {
        nearestObj = firstObj
        nearestCharIndex = 0
    }

    return if (nearestObj != null) LayoutCaret(nearestObj!!, nearestCharIndex) else null
}

fun Page.selectionCaretAtBegin(): LayoutCaret? {
    var firstObj: LayoutText? = null
    forEachChildRecursive(0F, 0F) { _, _, obj ->
        if (obj is LayoutText) {
            if (firstObj == null)
                firstObj = obj
        }
    }
    return if (firstObj != null) LayoutCaret(firstObj!!, 0) else null
}

fun Page.selectionCaretAtEnd(): LayoutCaret? {
    var lastObj: LayoutText? = null
    forEachChildRecursive(0F, 0F) { _, _, obj ->
        if (obj is LayoutText) {
            lastObj = obj
        }
    }
    return if (lastObj != null) LayoutCaret(lastObj!!, lastObj!!.charCount) else null
}

private fun Page.iterateSelectableObjects(
        action: (objLeft: Float, objTop: Float, obj: LayoutText) -> Unit
) {
    forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && obj.isSelectable() && obj !is LayoutSpaceText) {
            action(objLeft, objTop, obj)
        }
    }
}

/**
 * Исключает объекты, которые были добавлены в процессе форматирования текста, вроде переносов.
 * Выделять переносы можно только, если они находятся не на краю выделения, а между двух выделенных символов
 */
fun LayoutText.isSelectable() = range.endInclusive > range.start

fun Page.clickableObjectAt(x: Float, y: Float, radius: Float): LayoutObject? {
    var nearestObj: LayoutObject? = null
    var nearestSqrDistance = Float.MAX_VALUE
    val sqrRadius = radius * radius
    forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
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

fun Page.topOf(caret: LayoutCaret) = topOf(caret.obj) + PositionF(caret.obj.charOffset(caret.charIndex), 0F)
fun Page.bottomOf(caret: LayoutCaret) = topOf(caret.obj) + PositionF(caret.obj.charOffset(caret.charIndex), caret.obj.height)

fun Page.topOf(obj: LayoutObject): PositionF {
    var objLeft = 0F
    var objTop = 0F
    var found = false
    forEachChildRecursive(0F, 0F) { itObjLeft, itObjTop, itObj ->
        if (itObj == obj) {
            objLeft = itObjLeft
            objTop = itObjTop
            found = true
        }
    }
    return if (found) PositionF(objLeft, objTop) else throw IllegalArgumentException()
}

data class LayoutCaret(val obj: LayoutText, val charIndex: Int)

// порядок проверок составлен таким образом, чтобы исключить переносы на краю выделения
fun beginIndexOfSelectedChar(layoutText: LayoutText, selectionBegin: Location) = when {
    selectionBegin >= layoutText.range.endInclusive -> layoutText.charCount
    selectionBegin <= layoutText.range.start -> 0
    else -> layoutText.charIndex(selectionBegin)
}

// порядок проверок составлен таким образом, чтобы исключить переносы на краю выделения
fun endIndexOfSelectedChar(layoutText: LayoutText, selectionEnd: Location) = when {
    selectionEnd <= layoutText.range.start -> 0
    selectionEnd >= layoutText.range.endInclusive -> layoutText.charCount
    else -> layoutText.charIndex(selectionEnd)
}