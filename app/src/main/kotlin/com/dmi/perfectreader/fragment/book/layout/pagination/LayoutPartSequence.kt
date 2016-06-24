package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.util.range.indexOfNearestRange
import com.dmi.util.collection.SequenceEntry as Entry

class LayoutPartSequence(val layoutSequence: LocatedSequence<LayoutObject>) : LocatedSequence<LayoutPart> {
    override fun get(location: Location): Entry<LayoutPart> = partAt(layoutSequence[location], location)

    private fun partAt(obj: Entry<LayoutObject>, location: Location): PartEntry {
        val parts = splitIntoParts(obj.item)
        val index = parts.indexOfNearestRange({ range }, location)
        return PartEntry(parts, index, obj)
    }

    private fun partAtBegin(obj: Entry<LayoutObject>): PartEntry {
        val parts = splitIntoParts(obj.item)
        return PartEntry(parts, 0, obj)
    }

    private fun partAtEnd(obj: Entry<LayoutObject>): PartEntry {
        val parts = splitIntoParts(obj.item)
        return PartEntry(parts, parts.size - 1, obj)
    }

    private inner class PartEntry(
            val parts: List<LayoutPart>,
            val index: Int,
            val obj: Entry<LayoutObject>
    ) : Entry<LayoutPart> {
        override val item = parts[index]
        override val hasPrevious = index > 0 || obj.hasPrevious
        override val hasNext = index < parts.size - 1 || obj.hasNext

        override val previous: Entry<LayoutPart>
            get() = if (index > 0) PartEntry(parts, index - 1, obj) else partAtEnd(obj.previous)

        override val next: Entry<LayoutPart>
            get() = if (index < parts.size - 1) PartEntry(parts, index + 1, obj) else partAtBegin(obj.next)
    }
}