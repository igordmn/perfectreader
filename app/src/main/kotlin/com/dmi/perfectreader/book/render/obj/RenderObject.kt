package com.dmi.perfectreader.book.render.obj

import android.graphics.Canvas
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.util.graphic.Rect

abstract class RenderObject {
    abstract fun dirtyRect(oldContext: Context, newContext: Context): Rect?
    abstract fun paint(canvas: Canvas, context: Context)

    class Context(val selection: LocationRange?)
}