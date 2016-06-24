package com.dmi.perfectreader.fragment.book.pagination.column

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.part.LayoutPart
import com.dmi.util.collection.SequenceEntry as Entry

class LayoutColumnSequence(
        val partSequence: LocatedSequence<LayoutPart>,
        val columnHeight: Float
) : LocatedSequence<LayoutColumn> {
    val LAST_COLUMN_MIN_HEIGHT = columnHeight / 2

    override fun get(location: Location): Entry<LayoutColumn> = makeCurrent(partSequence[location])

    private fun makeCurrent(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addBottomParts().addTopPartsForLastColumn()
    private fun makePrevious(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addTopParts().addBottomParts()
    private fun makeNext(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addBottomParts()

    private fun initialColumn(part: Entry<LayoutPart>) = ColumnEntry(singlePartColumn(part.item), part, part)

    private fun ColumnEntry.addTopParts() = addTopParts(columnHeight)

    private fun ColumnEntry.addTopPartsForLastColumn() = if (!hasNext) {
        addTopParts(LAST_COLUMN_MIN_HEIGHT)
    } else {
        this
    }

    private fun ColumnEntry.addTopParts(maxHeight: Float): ColumnEntry {
        var column = this

        var part = firstPart
        while (part.hasPrevious) {
            part = part.previous

            val enlargedColumn = part mergeColumn column

            if (enlargedColumn.height <= maxHeight || enlargedColumn.height == column.height) {
                column = enlargedColumn
            } else {
                break
            }
        }

        return column
    }

    private fun ColumnEntry.addBottomParts(): ColumnEntry {
        var column = this

        var part = lastPart
        while (part.hasNext) {
            part = part.next

            val enlargedColumn = column mergePart part

            if (enlargedColumn.height <= columnHeight || enlargedColumn.height == column.height) {
                column = enlargedColumn
            } else {
                break
            }
        }

        return column
    }

    private infix fun ColumnEntry.mergePart(part: Entry<LayoutPart>) = ColumnEntry(
            item merge part.item,
            firstPart,
            part
    )

    private infix fun Entry<LayoutPart>.mergeColumn(column: ColumnEntry) = ColumnEntry(
            item merge column.item,
            this,
            column.lastPart
    )

    private inner class ColumnEntry(
            val column: LayoutColumn,
            val firstPart: Entry<LayoutPart>,
            val lastPart: Entry<LayoutPart>
    ) : Entry<LayoutColumn> {
        override val item = column
        override val hasPrevious = firstPart.hasPrevious
        override val hasNext = lastPart.hasNext

        override val previous: Entry<LayoutColumn> get() = makePrevious(firstPart.previous)
        override val next: Entry<LayoutColumn> get() = makeNext(lastPart.next)

        val height = column.height
    }
}