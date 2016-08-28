package com.dmi.perfectreader.fragment.menu

import com.dmi.perfectreader.fragment.book.Book
import com.dmi.util.android.base.BaseViewModel
import rx.Observable

class Menu(
        private val book: Book,
        private val closeAction: () -> Unit
) : BaseViewModel() {
    val percentObservable: Observable<Double> = book.locationObservable.map { book.locationConverter.locationToPercent(it) }
    val pageNumberObservable: Observable<Int> = book.locationObservable.map { book.locationConverter.locationToPageNumber(it) }

    val numberOfPages: Int get() = book.locationConverter.numberOfPages

    fun close() = closeAction()
    fun showSettings() = close()
    fun goPercent(percent: Double) = book.goLocation(book.locationConverter.percentToLocation(percent))
    fun goPageNumber(pageNumber: Int) = book.goLocation(book.locationConverter.pageNumberToLocation(pageNumber))
}