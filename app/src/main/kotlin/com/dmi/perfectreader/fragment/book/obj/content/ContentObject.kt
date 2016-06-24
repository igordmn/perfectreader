package com.dmi.perfectreader.fragment.book.obj.content

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import java.io.Serializable

abstract class ContentObject(val range: LocationRange) : Serializable {
    /**
     * Используется для определения положения в процентах
     *
     * Для текста длина равна количеству символов.
     * Для изображений равна 32.
     * Для контейнеров равна сумме дочерних объектов.
     */
    abstract val length: Double

    abstract fun configure(config: LayoutConfig): ComputedObject
}

abstract class ComputedObject(val range: LocationRange)