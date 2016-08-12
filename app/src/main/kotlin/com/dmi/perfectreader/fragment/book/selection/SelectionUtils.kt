package com.dmi.perfectreader.fragment.book.selection

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationDistance
import com.dmi.perfectreader.fragment.book.location.distance
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.sqrDistanceToRect

/**
 * Поиск локации ближайшей нижней половины каретки по y, либо по x (если y одинаков)
 * @param oppositeLocation сюда передается selectionRange.begin, если ищется selectionRange.end, и наоборот
 */
fun selectionLocationNearestTo(page: Page, x: Float, y: Float, oppositeLocation: Location): Location? {
    var nearestLocation: Location? = null
    var nearestXDistance = Float.MAX_VALUE
    var nearestYDistance = Float.MAX_VALUE
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && isSelectable(obj)) {
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
                        nearestLocation = obj.charLocation(i)
                        nearestXDistance = xDistance
                        nearestYDistance = yDistance
                    }
                }
            }
        }
    }
    return nearestLocation
}

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

fun selectionHandlePositionAt(page: Page, location: Location, alignLeft: Boolean): PositionF? {
    var nearestPositionFound = false
    var nearestPositionX = 0F
    var nearestPositionY = 0F
    var nearestLocationDistance = LocationDistance.MAX
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && isSelectable(obj)) {
            for (i in 0..obj.charCount) {
                if (alignLeft && i < obj.charCount || !alignLeft && i > 0) {
                    val charLocation = obj.charLocation(i)
                    val locationDistance = distance(location, charLocation)
                    if (locationDistance <= nearestLocationDistance) {
                        nearestPositionFound = true
                        nearestPositionX = objLeft + obj.charOffset(i)
                        nearestPositionY = objTop + obj.height
                        nearestLocationDistance = locationDistance
                    }
                }
            }
        }
    }
    return if (nearestPositionFound) PositionF(nearestPositionX, nearestPositionY) else null
}

fun selectionCharAt(page: Page, x: Float, y: Float): LayoutChar? {
    var nearestObj: LayoutText? = null
    var nearestCharIndex = -1
    var nearestDistance = Float.MAX_VALUE
    page.forEachChildRecursive(0F, 0F) { objLeft, objTop, obj ->
        if (obj is LayoutText && isSelectable(obj)) {
            val objRight = objLeft + obj.width
            val objBottom = objTop + obj.height

            val distance = sqrDistanceToRect(x, y, objLeft, objTop, objRight, objBottom)
            if (distance <= nearestDistance) {
                nearestObj = obj
                nearestCharIndex = charIndexAt(obj, objLeft, x)
                nearestDistance = distance
            }
        }
    }
    return if (nearestObj != null) LayoutChar(nearestObj!!, nearestCharIndex) else null
}

private fun charIndexAt(obj: LayoutText, objX: Float, x: Float): Int {
    for (i in 0..obj.charCount - 1) {
        if (x < objX + obj.charOffset(i))
            return i
    }

    return obj.charCount - 1
}

data class LayoutChar(val obj: LayoutText, val charIndex: Int)

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