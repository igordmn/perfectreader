package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.util.ext.cache

class CachedLayouter(private val layouter: ObjectLayouter<ConfiguredObject, LayoutObject>) : ObjectLayouter<ConfiguredObject, LayoutObject> {
    private val layoutObjects = cache(weakValues = true) { key: CacheKey ->
        layouter.layout(key.obj, key.space)
    }

    override fun layout(obj: ConfiguredObject, space: LayoutSpace) = layoutObjects[CacheKey(obj, space)]

    private data class CacheKey(val obj: ConfiguredObject, val space: LayoutSpace)
}