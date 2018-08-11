package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.obj.ContentBox
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.location.LocatedSequence
import com.dmi.perfectreader.book.location.flatMap
import com.dmi.util.ext.cache
import com.dmi.util.range.indexOfNearestRange
import java.util.*

private val leafsCache = cache<ContentObject, List<ContentObject>>(softValues = true) { obj ->
    ArrayList<ContentObject>().apply {
        collectLeafsOf(obj, this)
    }
}

private fun collectLeafsOf(obj: ContentObject, leafs: ArrayList<ContentObject>) {
    when (obj) {
        is ContentBox -> obj.children.forEach { collectLeafsOf(it, leafs) }
        is ContentFrame -> collectLeafsOf(obj.child, leafs)
        else -> leafs.add(obj)
    }
}

fun ContentLeafSequence(rootSequence: LocatedSequence<ContentObject>) = rootSequence.flatMap(
        transform = {
            leafsCache[it]
        },
        indexOf = {
            indexOfNearestRange({ range }, it)
        }
)