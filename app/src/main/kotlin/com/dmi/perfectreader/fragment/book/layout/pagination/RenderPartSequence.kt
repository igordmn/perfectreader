package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.util.range.indexOfNearestRange
import com.dmi.util.collection.SequenceEntry as Entry

class RenderPartSequence(val renderSequence: LocatedSequence<RenderObject>) : LocatedSequence<RenderPart> {
    override fun get(location: Location): Entry<RenderPart> = partAt(renderSequence[location], location)

    private fun partAt(obj: Entry<RenderObject>, location: Location): PartEntry {
        val parts = splitIntoParts(obj.item)
        val index = parts.indexOfNearestRange({ range }, location)
        return PartEntry(parts, index, obj)
    }

    private fun partAtBegin(obj: Entry<RenderObject>): PartEntry {
        val parts = splitIntoParts(obj.item)
        return PartEntry(parts, 0, obj)
    }

    private fun partAtEnd(obj: Entry<RenderObject>): PartEntry {
        val parts = splitIntoParts(obj.item)
        return PartEntry(parts, parts.size - 1, obj)
    }

    private inner class PartEntry(
            val parts: List<RenderPart>,
            val index: Int,
            val obj: Entry<RenderObject>
    ) : Entry<RenderPart> {
        override val item = parts[index]
        override val hasPrevious = index > 0 || obj.hasPrevious
        override val hasNext = index < parts.size - 1 || obj.hasNext

        override val previous: Entry<RenderPart>
            get() = if (index > 0) PartEntry(parts, index - 1, obj) else partAtEnd(obj.previous)

        override val next: Entry<RenderPart>
            get() = if (index < parts.size - 1) PartEntry(parts, index + 1, obj) else partAtBegin(obj.next)
    }
}