package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange

abstract class RenderObject(
        val width: Float,
        val height: Float,
        val children: List<RenderChild>,
        val range: LocationRange
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
    open fun internalMargins() = Margins.Companion.ZERO

    class Margins(val left: Float, val right: Float, val top: Float, val bottom: Float) {
        companion object {
            val ZERO = Margins(0F, 0F, 0F, 0F)
        }
    }
}