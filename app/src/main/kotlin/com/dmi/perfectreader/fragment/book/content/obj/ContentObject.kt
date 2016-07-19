package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.location.LocationRange
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

    abstract fun configure(config: LayoutConfig): ConfiguredObject
}

abstract class ConfiguredObject(val range: LocationRange)