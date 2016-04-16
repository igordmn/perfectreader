package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.page.LocatedSequence.Entry
import com.dmi.perfectreader.location.BookLocation
import com.dmi.perfectreader.layout.pagination.RenderPage
import com.dmi.perfectreader.layout.pagination.RenderRow
import com.dmi.perfectreader.layout.pagination.merge
import com.dmi.perfectreader.layout.pagination.singleRowPage

class BookPages(val rows: LocatedSequence<RenderRow>, val pageHeight: Float) : LocatedSequence<RenderPage> {
    override fun get(location: BookLocation): Entry<RenderPage> = makeCurrent(rows[location])

    private fun makeCurrent(initialRow: Entry<RenderRow>) = initialPage(initialRow).addBottomRows().addTopRows()
    private fun makePrevious(initialRow: Entry<RenderRow>) = initialPage(initialRow).addTopRows().addBottomRows()
    private fun makeNext(initialRow: Entry<RenderRow>) = initialPage(initialRow).addBottomRows()

    private fun initialPage(row: Entry<RenderRow>) = PageEntry(singleRowPage(row.item), row, row)

    private fun PageEntry.addTopRows(): PageEntry {
        var page = this

        var row = lastRow
        while (row.hasPrevious) {
            row = row.previous

            val enlargedPage = row mergePage page

            if (enlargedPage.height <= pageHeight || enlargedPage.height == page.height) {
                page = enlargedPage
            } else {
                break
            }
        }

        return page
    }

    private fun PageEntry.addBottomRows(): PageEntry {
        var page = this

        var row = lastRow
        while (row.hasNext) {
            row = row.next

            val enlargedPage = page mergeRow row

            if (enlargedPage.height <= pageHeight || enlargedPage.height == page.height) {
                page = enlargedPage
            } else {
                break
            }
        }

        return page
    }

    private infix fun PageEntry.mergeRow(row: Entry<RenderRow>) = PageEntry(
            item merge row.item,
            firstRow,
            row
    )

    private infix fun Entry<RenderRow>.mergePage(page: PageEntry) = PageEntry(
            item merge page.item,
            this,
            page.lastRow
    )

    private inner class PageEntry(
            val page: RenderPage,
            val firstRow: Entry<RenderRow>,
            val lastRow: Entry<RenderRow>
    ) : Entry<RenderPage> {
        override val item = page
        override val hasPrevious = firstRow.hasPrevious
        override val hasNext = lastRow.hasNext

        override val previous: Entry<RenderPage>
            get() = makePrevious(firstRow.previous)

        override val next: Entry<RenderPage>
            get() = makeNext(lastRow.next)

        val height = page.height
    }
}