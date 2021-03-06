package com.dmi.perfectreader.book.layout.obj

import com.dmi.perfectreader.book.content.location.LocationRange

abstract class LayoutObject(
        val width: Float,
        val height: Float,
        val children: List<LayoutChild>,
        val range: LocationRange,
        val pageBreakBefore: Boolean = false
) {
    /**
     * Необходимо для механизма разделения на страницы.
     * true - для текстовых строк, изображений, и всех тех объектов, которые необходимо рисовать на экране целиком
     * false - для контейнеров, таблиц, и всех тех объектов, которые можно рисовать частично (обрезав, например верхнюю границу)
     */
    open fun canBeSeparated() = false

    /**
     * Необходимо для механизма разделения на страницы.
     * Ненулевые margins означают, что, если элемент находит сверху или снизу страницы, то можно обрезать margins этого элемента.
     * К примеру, если у элемента верхняя граница 16, то можно расположить элемент с позицией y = -16
     */
    open fun internalMargins() = Margins.ZERO

    open fun isClickable() = false

    class Margins(val left: Float, val right: Float, val top: Float, val bottom: Float) {
        companion object {
            val ZERO = Margins(0F, 0F, 0F, 0F)
        }
    }

    override fun toString() = childLineOrNull(this)?.toString() ?: "[obj]"

    private fun childLineOrNull(obj: LayoutObject): LayoutLine? {
        return if (obj.children.isNotEmpty()) {
            val child = obj.children[0].obj
            return child as? LayoutLine ?: childLineOrNull(child)
        } else {
            null
        }
    }

    fun forEachChildRecursive(x: Float, y: Float, action: (x: Float, y: Float, obj: LayoutObject) -> Unit) {
        for (child in children) {
            val objX = x + child.x
            val objY = y + child.y
            val obj = child.obj
            action(objX, objY, obj)
            obj.forEachChildRecursive(objX, objY, action)
        }
    }
}