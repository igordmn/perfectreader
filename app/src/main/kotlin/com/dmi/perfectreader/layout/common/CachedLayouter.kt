package com.dmi.perfectreader.layout.image

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.render.RenderObject
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

class CachedLayouter(private val layouter: Layouter<LayoutObject, RenderObject>) : Layouter<LayoutObject, RenderObject> {
    private val renderObjects = CacheBuilder.newBuilder()
            .weakValues()
            .build(
                    CacheLoader.from<CacheKey, RenderObject> {
                        layouter.layout(it!!.obj, it.space)
                    }
            )

    override fun layout(obj: LayoutObject, space: LayoutSpace): RenderObject {
        return object {
            fun layout() = renderObjects.get(CacheKey(obj, space))
        }.layout()
    }

    data class CacheKey(val obj: LayoutObject, val space: LayoutSpace)
}
