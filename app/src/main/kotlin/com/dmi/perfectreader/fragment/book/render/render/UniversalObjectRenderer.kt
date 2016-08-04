package com.dmi.perfectreader.fragment.book.render.render

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutFrame
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.render.obj.RenderFrame
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderText
import java.util.*

class UniversalObjectRenderer(private val imageRenderer: ImageRenderer) {
    fun render(x: Float, y: Float, obj: LayoutObject, objects: ArrayList<RenderObject>) {
        when (obj) {
            is LayoutFrame -> objects.add(RenderFrame(x, y, obj))
            is LayoutImage -> imageRenderer.render(x, y, obj, objects)
            is LayoutText -> objects.add(RenderText(x, y, obj))
        }
    }
}