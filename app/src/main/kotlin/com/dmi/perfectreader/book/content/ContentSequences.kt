package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.flatMap
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.content.obj.ContentBox
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.content.obj.param.FormatConfig
import com.dmi.util.collection.ListSequenceEntry
import com.dmi.util.ext.cache
import com.dmi.util.range.indexOfNearestRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun LocatedSequence<ContentObject>.configure(config: FormatConfig) = map {
    withContext(Dispatchers.Default) {
        it.configure(config)
    }
}

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

fun contentLeafSequence(rootSequence: LocatedSequence<ContentObject>) = rootSequence.flatMap(
        transform = {
            leafsCache[it]
        },
        indexOf = {
            indexOfNearestRange({ range }, it)
        }
)

class ContentObjectSequence(private val objects: List<ContentObject>) : LocatedSequence<ContentObject> {
    init {
        require(objects.isNotEmpty())
    }

    override suspend fun get(location: Location) = ListSequenceEntry(objects, indexOf(location))

    private fun indexOf(location: Location) = objects.indexOfNearestRange({ range }, location)
}