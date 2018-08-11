package com.dmi.perfectreader.reader

import android.net.Uri
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.book
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.control.Control
import com.dmi.perfectreader.menu.Menu
import com.dmi.perfectreader.reader.action.ReaderActions
import com.dmi.perfectreader.reader.action.ReaderSettingActionID
import com.dmi.perfectreader.selection.Selection
import com.dmi.util.scope.Scoped
import com.dmi.util.system.ApplicationWindow

suspend fun reader(main: Main, window: ApplicationWindow, uri: Uri): Reader {
    val book = book(main, uri)
    return Reader(main, window, book)
}

class Reader(
        private val main: Main,
        window: ApplicationWindow,
        book: Book
) : Scoped by Scoped.Impl() {
    val actions = ReaderActions(main, window, this)
    val book: Book by scope.disposable(book)
    val control: Control = Control(main, this)
    var selection: Selection? by scope.value(null)
    var menu: Menu? by scope.value(null)

    fun selection(range: LocationRange?): Selection? = if (range != null) {
        val deselect = {
            selection = null
        }
        Selection(main, book, range, deselect)
    } else {
        null
    }

    var actionPopup: ActionPopupModel by scope.value(ActionPopupModel.INVISIBLE)
        private set

    fun toggleMenu() {
        menu = if (menu == null) menu() else null
    }

    private fun menu(): Menu {
        val closeMenu = {
            menu = null
        }
        return Menu(book, closeMenu)
    }

    fun showActionPopup(id: ReaderSettingActionID, value: Any) {
        actionPopup = ActionPopupModel.visible(id, value)
    }

    fun hideActionPopup() {
        actionPopup = ActionPopupModel.INVISIBLE
    }

    class ActionPopupModel(val isVisible: Boolean, val id: ReaderSettingActionID, val value: Any) {
        companion object {
            val INVISIBLE = ActionPopupModel(isVisible = false, id = ReaderSettingActionID.NONE, value = "")
            fun visible(id: ReaderSettingActionID, value: Any) = ActionPopupModel(true, id, value)
        }
    }
}