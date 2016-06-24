package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject

interface ObjectPainter<R : LayoutObject> {
    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     *
     * Этот метод может длиться долго (к примеру, загружать картинки с карты памяти), так что его нужно вызывать не в render-потоке
     */
    fun paintItself(obj: R, canvas: Canvas, context: PaintContext)
}