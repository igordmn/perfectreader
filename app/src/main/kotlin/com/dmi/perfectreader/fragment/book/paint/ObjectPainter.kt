package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext

interface ObjectPainter<in R : LayoutObject> {
    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     *
     * Этот метод может длиться долго (к примеру, загружать картинки с карты памяти), так что его нужно вызывать не в render-потоке
     */
    fun paintItself(obj: R, context: PageContext, canvas: Canvas)
}