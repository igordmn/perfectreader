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
import com.dmi.perfectreader.menu.MenuState
import com.dmi.perfectreader.selection.Selection
import com.dmi.perfectreader.selection.SelectionState
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.perfectreader.settingsui.SettingsUIState
import com.dmi.util.lang.map
import com.dmi.util.lang.unsupported
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.scope.observableProperty
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

suspend fun reader(main: Main, uri: Uri, state: ReaderState): Reader {
    val book = book(main, uri)
    return Reader(main, book, state)
}

class Reader(
        private val main: Main,
        book: Book,
        val state: ReaderState,
        scope: Scope = Scope()
) : Screen by Screen(scope) {
    val actions = Actions(main, this)
    val book: Book by scope.observableDisposable(book)
    val control: Control by scope.observableDisposable(Control(main, this))
    var selection: Selection? by scope.observableDisposableProperty(map(state::selection, ::Selection, ::state))
        private set
    var popup: Screen? by observableProperty(map(state::popup, ::createPopup, ::popupState))
        private set
    var performingAction: PerformingAction? by observable(null)

    fun select(range: LocationRange?) {
        selection = if (range != null) Selection(SelectionState(range)) else null
    }

    fun deselect() {
        selection = null
    }

    fun showMenu() {
        popup = Menu()
    }

    private fun showSettings() {
        popup = SettingsUI()
    }

    private fun hidePopup() {
        popup = null
    }

    private fun createPopup(state: Any): Screen = when (state) {
        is MenuState -> Menu(state)
        is SettingsUIState -> SettingsUI(state)
        else -> unsupported(state)
    }

    private fun Selection(state: SelectionState) = Selection(main, book, ::deselect, state)
    private fun Menu(state: MenuState = MenuState()) = Menu(::showSettings, ::hidePopup, state)
    private fun SettingsUI(state: SettingsUIState = SettingsUIState()) = SettingsUI(::hidePopup, this, state)

    private fun popupState(model: Screen): Any = when (model) {
        is Menu -> model.state
        is SettingsUI -> model.state
        else -> unsupported(model)
    }

    private fun state(model: Selection) = model.state
}

@Serializable
class ReaderState(
        var selection: SelectionState? = null,
        var popup: Any? = null
)