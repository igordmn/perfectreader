package com.dmi.perfectreader.render

import android.graphics.Canvas

abstract class RenderObject(private val width: Float, private val height: Float, private val children: List<RenderChild>) {

    fun width(): Float {
        return width
    }

    fun height(): Float {
        return height
    }

    fun children(): List<RenderChild> {
        return children
    }

    fun child(index: Int): RenderChild {
        return children[index]
    }

    /**
     * Необходимо для механизма разделения на страницы.
     * true - для текстовых строк, изображений, и всех тех объектов, которые необходимо рисовать на экране целиком
     * false - для контейнеров, таблиц, и всех тех объектов, которые можно рисовать частично (обрезав, например верхнюю границу)
     */
    abstract fun canPartiallyPainted(): Boolean

    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     */
    open fun paintItself(canvas: Canvas) {
    }

    fun paintRecursive(canvas: Canvas) {
        paintItself(canvas)
        for (i in 0..children.size - 1) {
            val child = children[i]
            canvas.translate(child.x(), child.y())
            child.`object`().paintRecursive(canvas)
            canvas.translate(-child.x(), -child.y())
        }
    }
}
