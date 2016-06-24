package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.content.obj.ContentObject
import com.dmi.util.collection.ListSequenceEntry
import com.dmi.util.collection.SequenceEntry
import com.dmi.util.range.indexOfNearestRange

class ContentObjectSequence(private val objects: List<ContentObject>) : LocatedSequence<ContentObject> {
    init {
        require(objects.size > 0)
    }

    override fun get(location: Location): SequenceEntry<ContentObject> =
            ListSequenceEntry(objects, indexOf(location))

    private fun indexOf(location: Location) = objects.indexOfNearestRange({ range }, location)
}