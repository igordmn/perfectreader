package com.dmi.perfectreader.book.selection

import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.wordBeginBefore
import com.dmi.perfectreader.book.content.wordEndAfter
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.location.Location
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.sqrDistanceToRect

fun selectionInitialRange(content: Content, page: Page, x: Float, y: Float): LocationRange? {
    val caret = selectionCaretOfCharNearestTo(page, x, y)
    if (caret != null) {
        val range = LocationRange(caret.obj.charLocation(caret.charIndex), caret.obj.charLocation(caret.charIndex + 1))
        return selectionAlignToWords(content, range)
    } else {
        return null
    }
}

fun selectionCaretOfCharNearestTo(page: Page, x: Float, y: Float): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    iterateSelectableObjects(page) { objLeft, objTop, obj ->
        for (i in 0..obj.charCount - 1) {
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


fun newSelection(content: Content, page: Page, oldRange: LocationRange, isLeftHandle: Boolean, touchPosition: PositionF, selectWords: Boolean): NewSelectionResult {
    val result = newSelectionSelectChars(page, oldRange, isLeftHandle, touchPosition, selectWords)
    return if (selectWords) NewSelectionResult(selectionAlignToWords(content, result.range), result.isLeftHandle) else result
}

fun newSelectionSelectChars(page: Page, oldRange: LocationRange, isLeftHandle: Boolean, touchPosition: PositionF, excludeSpaces: Boolean): NewSelectionResult {
    val oppositeLocation = if (isLeftHandle) oldRange.end else oldRange.begin
    val selectionChar = selectionCaretNearestTo(page, touchPosition.x, touchPosition.y, oppositeLocation)
    return if (selectionChar != null) {
        val selectionLocation = selectionChar.obj.charLocation(selectionChar.charIndex)
        if (isLeftHandle) {
            if (selectionLocation <= oldRange.end) {
                NewSelectionResult(LocationRange(selectionLocation, oldRange.end), true)
            } else {
                NewSelectionResult(LocationRange(oldRange.end, selectionLocation), false)
            }
        } else {
            if (selectionLocation >= oldRange.begin) {
                NewSelectionResult(LocationRange(oldRange.begin, selectionLocation), false)
            } else {
                NewSelectionResult(LocationRange(selectionLocation, oldRange.begin), true)
            }
        }
    } else {
        NewSelectionResult(oldRange, isLeftHandle)
    }
}

data class NewSelectionResult(val range: LocationRange, val isLeftHandle: Boolean)

/**
 * Поиск каретки с ближайшей нижней половиной по y, либо по x (если y одинаков)
 * @param oppositeLocation сюда передается selectionRange.begin, если ищется selectionRange.end, и наоборот
 */
fun selectionCaretNearestTo(page: Page, x: Float, y: Float, oppositeLocation: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    iterateSelectableObjects(page) { objLeft, objTop, obj ->
        for (i in 0..obj.charCount) {
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
    }
    return if (nearestObj != null) LayoutCaret(nearestObj!!, nearestCharIndex) else null
}

fun selectionCaretAtLeft(page: Page, location: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var lastObj: LayoutText? = null

    iterateSelectableObjects(page) { objLeft, objTop, obj ->
        if (nearestObj == null) {
            when {
                location < obj.range.begin -> {
                    nearestObj = obj
                    nearestCharIndex = 0
                }
                obj.range.end < location -> Unit
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

fun selectionCaretAtRight(page: Page, location: Location): LayoutCaret? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var firstObj: LayoutText? = null

    iterateSelectableObjects(page) { objLeft, objTop, obj ->
        if (firstObj == null)
            firstObj = obj

        when {
            location < obj.range.begin -> Unit
            obj.range.end < location -> {
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

fun selectionCaretAtBegin(page: Page): LayoutCaret? {
    var firstObj: LayoutText? = null
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText) {
            if (firstObj == null)
                firstObj = obj
        }
    }
    return if (firstObj != null) LayoutCaret(firstObj!!, 0) else null
}

fun selectionCaretAtEnd(page: Page): LayoutCaret? {
    var lastObj: LayoutText? = null
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText) {
            lastObj = obj
        }
    }
    return if (lastObj != null) LayoutCaret(lastObj!!, lastObj!!.charCount) else null
}

private fun iterateSelectableObjects(
        page: Page,
        action: (objLeft: Float, objTop: Float, obj: LayoutText) -> Unit
) {
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && isSelectable(obj) && obj !is LayoutSpaceText) {
            action(objLeft, objTop, obj)
        }
    }
}

/**
 * если в range нету слов, то возвращает range
 * если есть, то смещаем левую границу к началу первого слова, правую границу к концу последнего слова
 * (границы могут сместить как влево, так и вправо, если range содержит слово не целиком)
 */
fun selectionAlignToWords(content: Content, range: LocationRange): LocationRange {
    val firstWordEnd = content.wordEndAfter(range.begin)
    val lastWordBegin = content.wordBeginBefore(range.end)
    val firstWordBegin = if (firstWordEnd != null) content.wordBeginBefore(firstWordEnd) else null
    val lastWordEnd = if (lastWordBegin != null) content.wordEndAfter(lastWordBegin) else null
    if (firstWordBegin != null && lastWordEnd != null && lastWordEnd > firstWordBegin) {
        return LocationRange(firstWordBegin, lastWordEnd)
    } else {
        return range
    }
}

data class LayoutCaret(val obj: LayoutText, val charIndex: Int)

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

fun topOf(page: Page, caret: LayoutCaret) = topOf(page, caret.obj) + PositionF(caret.obj.charOffset(caret.charIndex), 0F)
fun bottomOf(page: Page, caret: LayoutCaret) = topOf(page, caret.obj) + PositionF(caret.obj.charOffset(caret.charIndex), caret.obj.height)

fun topOf(page: Page, obj: LayoutObject): PositionF {
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