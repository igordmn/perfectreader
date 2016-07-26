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

    /**
     * - установка неустановленных параметров (например, если textSize = null, то ставится стандартное значение)
     * - применение настроек
     * - привязка к конкретным ресурсам (например, если указан шрифт "Roboto" в виде простого названияи и стиль Italic,
     *   эта функция возьмет конкретный шрифт на карте памяти /sdcard/Fonts/RobotoI.ttf)
     * - преобразование dip (device independent pixels) в px
     */
    abstract fun configure(config: LayoutConfig): ConfiguredObject
}

abstract class ConfiguredObject(val range: LocationRange)