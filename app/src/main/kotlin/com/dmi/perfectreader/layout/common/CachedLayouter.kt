package com.dmi.perfectreader.layout.common

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.render.RenderObject
import com.dmi.util.libext.cache

class CachedLayouter(private val layouter: Layouter<LayoutObject, RenderObject>) : Layouter<LayoutObject, RenderObject> {
    private val renderObjects = cache<CacheKey, RenderObject>(weakValues = true) {
        layouter.layout(it.obj, it.space)
    }

    override fun layout(obj: LayoutObject, space: LayoutSpace) = renderObjects.get(CacheKey(obj, space))

    private data class CacheKey(val obj: LayoutObject, val space: LayoutSpace)
}
