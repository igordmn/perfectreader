package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.util.graphic.SizeF
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import com.dmi.util.collection.SequenceEntry as Entry

/**
 * using not CommonPool, because ObjectLayouter not thread safe (it is using Reusable variables)
 */
private val layoutContext = newSingleThreadContext("layout")

fun LocatedSequence<ConfiguredObject>.layout(
        layouter: ObjectLayouter<ConfiguredObject, LayoutObject>,
        pageSize: SizeF
) = map {
    withContext(layoutContext) {
        layouter.layout(it, LayoutSpace.root(pageSize))
    }
}