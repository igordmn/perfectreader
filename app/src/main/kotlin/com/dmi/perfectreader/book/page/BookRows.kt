package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.page.LocatedSequence.Entry
import com.dmi.perfectreader.location.BookLocation
import com.dmi.perfectreader.location.indexOfNearest
import com.dmi.perfectreader.layout.renderobj.RenderObject
import com.dmi.perfectreader.layout.pagination.RenderRow
import com.dmi.perfectreader.layout.pagination.splitIntoRows

class BookRows(val objects: LocatedSequence<RenderObject>) : LocatedSequence<RenderRow> {
    override fun get(location: BookLocation): Entry<RenderRow> = rowAt(objects[location], location)

    private fun rowAt(obj: Entry<RenderObject>, location: BookLocation): RowEntry {
        val rows = splitIntoRows(obj.item)
        val index = rows.indexOfNearest({ range }, location)
        return RowEntry(rows, index, obj)
    }

    private fun rowAtBegin(obj: Entry<RenderObject>): RowEntry {
        val rows = splitIntoRows(obj.item)
        return RowEntry(rows, 0, obj)
    }

    private fun rowAtEnd(obj: Entry<RenderObject>): RowEntry {
        val rows = splitIntoRows(obj.item)
        return RowEntry(rows, rows.size - 1, obj)
    }

    private inner class RowEntry(
            val rows: List<RenderRow>,
            val index: Int,
            val obj: Entry<RenderObject>
    ) : Entry<RenderRow> {
        override val item = rows[index]
        override val hasPrevious = index > 0 || obj.hasPrevious
        override val hasNext = index < rows.size - 1 || obj.hasNext

        override val previous: Entry<RenderRow>
            get() = if (index > 0) RowEntry(rows, index - 1, obj) else rowAtEnd(obj.previous)

        override val next: Entry<RenderRow>
            get() = if (index < rows.size - 1) RowEntry(rows, index + 1, obj) else rowAtBegin(obj.next)
    }
}