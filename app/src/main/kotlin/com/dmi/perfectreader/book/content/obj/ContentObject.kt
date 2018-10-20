package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredObject
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

interface ContentObject {
    val range: LocationRange

    /**
     * Используется для определения положения в процентах
     *
     * Для текста длина равна количеству символов.
     * Для изображений равна 32.
     * Для контейнеров равна сумме дочерних объектов.
     */
    val length: Double

    /**
     * - установка неустановленных параметров (например, если textSize = null, то ставится стандартное значение)
     * - применение настроек
     * - привязка к конкретным ресурсам (например, если указан шрифт "Roboto" в виде простого названияи и стиль Italic,
     *   эта функция возьмет конкретный шрифт на карте памяти /sdcard/Fonts/RobotoI.ttf)
     * - преобразование dip (device independent pixels) в px
     */
    fun configure(config: ContentConfig): ConfiguredObject
}