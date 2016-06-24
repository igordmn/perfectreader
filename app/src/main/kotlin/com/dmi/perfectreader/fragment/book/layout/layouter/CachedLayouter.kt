package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.util.ext.cache

class CachedLayouter(private val layouter: Layouter<ComputedObject, RenderObject>) : Layouter<ComputedObject, RenderObject> {
    private val renderObjects = cache(weakValues = true) { key: CacheKey ->
        layouter.layout(key.obj, key.space)
    }

    override fun layout(obj: ComputedObject, space: LayoutSpace) = renderObjects[CacheKey(obj, space)]

    private data class CacheKey(val obj: ComputedObject, val space: LayoutSpace)
}