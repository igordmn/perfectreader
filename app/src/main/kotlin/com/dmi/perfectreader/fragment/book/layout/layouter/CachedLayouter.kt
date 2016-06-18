package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.util.ext.cache

class CachedLayouter(private val layouter: Layouter<LayoutObject, RenderObject>) : Layouter<LayoutObject, RenderObject> {
    private val renderObjects = cache(weakValues = true) { key: CacheKey ->
        layouter.layout(key.obj, key.space)
    }

    override fun layout(obj: LayoutObject, space: LayoutSpace) = renderObjects[CacheKey(obj, space)]

    private data class CacheKey(val obj: LayoutObject, val space: LayoutSpace)
}