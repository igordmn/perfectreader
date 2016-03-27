package com.dmi.perfectreader.render

import android.graphics.Canvas

abstract class RenderObject(
        val width: Float,
        val height: Float,
        val children: List<RenderChild>
) {
    fun child(index: Int) = children[index]

    /**
     * Необходимо для механизма разделения на страницы.
     * true - для текстовых строк, изображений, и всех тех объектов, которые необходимо рисовать на экране целиком
     * false - для контейнеров, таблиц, и всех тех объектов, которые можно рисовать частично (обрезав, например верхнюю границу)
     */
    open fun canPartiallyPainted() = false

    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     */
    open fun paintItself(canvas: Canvas) = Unit

    fun paintRecursive(canvas: Canvas) {
        paintItself(canvas)
        for (i in 0..children.size - 1) {
            with (children[i]) {
                canvas.translate(x, y)
                obj.paintRecursive(canvas)
                canvas.translate(-x, -y)
            }
        }
    }
}
