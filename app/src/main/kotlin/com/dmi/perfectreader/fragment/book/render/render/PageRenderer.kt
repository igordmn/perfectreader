package com.dmi.perfectreader.fragment.book.render.render

import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.render.obj.RenderObject
import com.dmi.perfectreader.fragment.book.render.obj.RenderPage
import java.util.*

class PageRenderer(private val objectRenderer: UniversalObjectRenderer) {
    fun render(page: Page): RenderPage {
        val objects = ArrayList<RenderObject>()
        page.forEachChildRecursive(0F, 0F) { objX, objY, obj ->
            objectRenderer.render(objX, objY, obj, objects)
        }
        return RenderPage(objects, page)
    }
}