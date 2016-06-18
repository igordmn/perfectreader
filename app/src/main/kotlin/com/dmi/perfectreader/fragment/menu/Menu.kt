package com.dmi.perfectreader.fragment.menu

import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.util.base.BaseViewModel

class Menu(
        private val book: Book,
        private val closeAction: () -> Unit
) : BaseViewModel() {
    val percentObservable = book.locationObservable.map { locationToPercent(it) }

    fun close() = closeAction()
    fun showSettings() = close()
    fun goPercent(percent: Double) = book.goLocation(percentToLocation(percent))


    private fun locationToPercent(location: Location) = book.locationConverter.locationToPercent(location)
    private fun percentToLocation(percent: Double) = book.locationConverter.percentToLocation(percent)
}