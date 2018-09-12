package com.dmi.perfectreader.reader

import android.net.Uri
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.action.Actions
import com.dmi.perfectreader.action.PerformingAction
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.book
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.control.Control
import com.dmi.perfectreader.menu.Menu
import com.dmi.perfectreader.selection.Selection
import com.dmi.perfectreader.settingschange.SettingsChange
import com.dmi.util.lang.then
import com.dmi.util.scope.Scoped
import com.dmi.util.scope.observable

suspend fun reader(main: Main, uri: Uri): Reader {
    val book = book(main, uri)
    return Reader(main, book)
}

class Reader(
        private val main: Main,
        book: Book
) : Scoped by Scoped.Impl() {
    val actions = Actions(main, this)
    val book: Book by scope.disposable(book)
    val control: Control by scope.disposable(Control(main, this))
    var selection: Selection? by scope.disposable(null)
    var menu: Menu? by observable(null)
    var settingsChange: SettingsChange? by observable(null)
    var performingAction: PerformingAction? by observable(null)

    fun createSelection(range: LocationRange?): Selection? = if (range != null) {
        val deselect = {
            selection = null
        }
        Selection(main, book, range, deselect)
    } else {
        null
    }

    fun toggleMenu() {
        menu = if (menu == null) createMenu() else null
    }

    private fun createMenu() = Menu(showSettings = ::hideMenu then ::showSettings, back = ::hideMenu)
    private fun createSettings() = SettingsChange(back = ::hideSettings)

    private fun hideMenu() {
        menu = null
    }

    private fun showSettings() {
        settingsChange = createSettings()
    }

    private fun hideSettings() {
        settingsChange = null
    }
}