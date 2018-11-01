package com.dmi.perfectreader.book.pagination.column

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.pagination.part.LayoutPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.dmi.util.collection.SequenceEntry as Entry

fun LocatedSequence<LayoutPart>.columns(columnHeight: Float) = LayoutColumnSequence(this, columnHeight)

class LayoutColumnSequence(
        private val partSequence: LocatedSequence<LayoutPart>,
        private val columnHeight: Float
) : LocatedSequence<LayoutColumn> {
    private val lastColumnMinHeight = columnHeight / 2

    override suspend fun get(location: Location): Entry<LayoutColumn> = makeCurrent(partSequence.get(location))

    private suspend fun makeCurrent(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addBottomParts().addTopPartsForLastColumn()
    private suspend fun makePrevious(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addTopParts().addBottomParts()
    private suspend fun makeNext(initialPart: Entry<LayoutPart>) = initialColumn(initialPart).addBottomParts()

    private fun initialColumn(part: Entry<LayoutPart>) = ColumnEntry(singlePartColumn(part.item), part, part)

    private suspend fun ColumnEntry.addTopParts() = addTopParts(columnHeight)

    private suspend fun ColumnEntry.addTopPartsForLastColumn() = if (!hasNext) {
        addTopParts(lastColumnMinHeight)
    } else {
        this
    }

    private suspend fun ColumnEntry.addTopParts(maxHeight: Float): ColumnEntry = withContext(Dispatchers.Default) {
        var column = this@addTopParts

        var part = firstPart
        while (part.hasPrevious && !part.item.pageBreakBefore) {
            part = part.previous()

            val enlargedColumn = part mergeColumn column

            if (enlargedColumn.height <= maxHeight || enlargedColumn.height == column.height) {
                column = enlargedColumn
            } else {
                break
            }
        }

        column
    }

    private suspend fun ColumnEntry.addBottomParts(): ColumnEntry = withContext(Dispatchers.Default) {
        var column = this@addBottomParts

        var part = lastPart

        while (part.hasNext) {
            part = part.next()

            if (part.item.pageBreakBefore)
                break

            val enlargedColumn = column mergePart part

            if (enlargedColumn.height <= columnHeight || enlargedColumn.height == column.height) {
                column = enlargedColumn
            } else {
                break
            }
        }

        column
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
            override val item: LayoutColumn,
            val firstPart: Entry<LayoutPart>,
            val lastPart: Entry<LayoutPart>
    ) : Entry<LayoutColumn> {
        override val hasPrevious = firstPart.hasPrevious
        override val hasNext = lastPart.hasNext

        override suspend fun previous(): Entry<LayoutColumn> = makePrevious(firstPart.previous())
        override suspend fun next(): Entry<LayoutColumn> = makeNext(lastPart.next())

        val height = item.height
    }
}