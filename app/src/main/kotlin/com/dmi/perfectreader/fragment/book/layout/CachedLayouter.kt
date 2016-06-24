package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.content.obj.ComputedObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.util.ext.cache

class CachedLayouter(private val layouter: ObjectLayouter<ComputedObject, LayoutObject>) : ObjectLayouter<ComputedObject, LayoutObject> {
    private val layoutObjects = cache(weakValues = true) { key: CacheKey ->
        layouter.layout(key.obj, key.space)
    }

    override fun layout(obj: ComputedObject, space: LayoutSpace) = layoutObjects[CacheKey(obj, space)]

    private data class CacheKey(val obj: ComputedObject, val space: LayoutSpace)
}