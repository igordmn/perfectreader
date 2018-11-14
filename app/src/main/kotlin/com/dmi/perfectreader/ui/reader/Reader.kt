package com.dmi.perfectreader.ui.reader

import android.net.Uri
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.ui.action.Actions
import com.dmi.perfectreader.ui.action.PerformingAction
import com.dmi.perfectreader.ui.book.Book
import com.dmi.perfectreader.ui.book.book
import com.dmi.perfectreader.ui.control.Control
import com.dmi.perfectreader.ui.menu.Menu
import com.dmi.perfectreader.ui.menu.MenuState
import com.dmi.perfectreader.ui.search.SearchUI
import com.dmi.perfectreader.ui.search.SearchUIState
import com.dmi.perfectreader.ui.selection.Selection
import com.dmi.perfectreader.ui.selection.SelectionState
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.SettingsUIState
import com.dmi.perfectreader.ui.tableofcontents.TableOfContentsUI
import com.dmi.perfectreader.ui.tableofcontents.TableOfContentsUIState
import com.dmi.util.lang.init
import com.dmi.util.lang.unsupported
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

suspend fun reader(context: ReaderContext, uri: Uri, close: () -> Unit, state: ReaderState): Reader {
    val book = book(context, uri)
    return Reader(context, book, close, state)
}

class Reader(
        private val context: ReaderContext,
        book: Book,
        private val close: () -> Unit,
        val state: ReaderState,
        scope: Scope = Scope()
) : Screen by Screen(scope) {
    val actions = Actions(context, this)
    val book: Book by scope.observableDisposable(book)
    val control: Control by scope.observableDisposable(Control(context, this))
    var selection: Selection? by scope.observableDisposableProperty(init(state::selection, ::Selection, ::state))
        private set
    var popup: Screen? by scope.observableDisposableProperty(init(state::popup, ::createPopup, ::popupState))
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

    fun showSettings() {
        popup = SettingsUI()
    }

    fun showTableOfContents() {
        popup = TableOfContentsUI()
    }

    fun showSearch() = showSearch("")

    fun showLibrary() {
        TODO()
    }

    fun showSearch(text: String) {
        popup = SearchUI(SearchUIState(text))
    }

    private fun hidePopup() {
        popup = null
    }

    private fun createPopup(state: Any): Screen = when (state) {
        is MenuState -> Menu(state)
        is SettingsUIState -> SettingsUI(state)
        is TableOfContentsUIState -> TableOfContentsUI(state)
        is SearchUIState -> SearchUI(state)
        else -> unsupported(state)
    }

    private fun Selection(state: SelectionState) = Selection(context, this, ::deselect, state)
    private fun Menu(state: MenuState = MenuState()) = Menu(
            book,
            ::showSettings,
            ::showTableOfContents,
            ::showSearch,
            ::hidePopup,
            close,
            state
    )
    private fun SettingsUI(state: SettingsUIState = SettingsUIState()) = SettingsUI(::hidePopup, this, state)
    private fun TableOfContentsUI(state: TableOfContentsUIState = TableOfContentsUIState()) = TableOfContentsUI(book, ::hidePopup, state)
    private fun SearchUI(state: SearchUIState = SearchUIState()) = SearchUI(book, ::hidePopup, state)

    private fun popupState(model: Screen): Any = when (model) {
        is Menu -> model.state
        is SettingsUI -> model.state
        is TableOfContentsUI -> model.state
        is SearchUI -> model.state
        else -> unsupported(model)
    }

    private fun state(model: Selection) = model.state
}

@Serializable
class ReaderState(
        var selection: SelectionState? = null,
        var popup: Any? = null
)