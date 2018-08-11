package com.dmi.perfectreader.menu

import com.dmi.perfectreader.book.Book
import com.dmi.util.scope.Scoped

class Menu(
        private val book: Book,
        val close: () -> Unit
)  : Scoped by Scoped.Impl() {
    val percent: Double by scope.cached { book.locations.locationToPercent(book.location) }
    val pageNumber: Int by scope.cached {  book.locations.locationToPageNumber(book.location) }
    val numberOfPages: Int by scope.cached { book.locations.numberOfPages }

    fun showSettings() = close()

    fun goPercent(percent: Double) = book.goLocation(book.locations.percentToLocation(percent))
    fun goPageNumber(pageNumber: Int) = book.goLocation(book.locations.pageNumberToLocation(pageNumber))
}