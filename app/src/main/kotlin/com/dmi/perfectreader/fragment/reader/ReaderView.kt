package com.dmi.perfectreader.fragment.reader

import android.content.Context
import android.view.ViewGroup
import com.dmi.perfectreader.R
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.BookView
import com.dmi.perfectreader.fragment.control.Control
import com.dmi.perfectreader.fragment.control.ControlView
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.menu.MenuView
import com.dmi.perfectreader.fragment.selection.Selection
import com.dmi.perfectreader.fragment.selection.SelectionView
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.find
import com.dmi.util.android.ext.addOrRemoveView
import com.dmi.util.android.widget.fadeTransition

class ReaderView(
        context: Context,
        model: Reader,
        createBook: (Book) -> BookView,
        createControl: (Control) -> ControlView,
        private val createSelection: (Selection) -> SelectionView,
        private val createMenu: (Menu) -> MenuView
) : BaseView(context, R.layout.fragment_reader) {
    private val menuContainer = find<ViewGroup>(R.id.menuContainer)
    private val actionPopupContainer = find<ViewGroup>(R.id.actionPopupContainer)
    private var selection: SelectionView? = null
    private var menu: MenuView? = null
    private var actionPopup: ActionPopup? = null

    init {
        addChild(createBook(model.book), R.id.bookContainer)
        addChild(createControl(model.control), R.id.controlContainer)

        menuContainer.layoutTransition = fadeTransition(300)
        actionPopupContainer.layoutTransition = fadeTransition(300)

        subscribe(model.selectionObservable) { it ->
            selection = toggleChildByModel(it, selection, R.id.selectionContainer, createSelection)
        }

        subscribe(model.menuObservable) { it ->
            menu = toggleChildByModel(it, menu, R.id.menuContainer, createMenu)
        }

        subscribe(model.actionPopupObservable) { it ->
            actionPopup = actionPopupContainer.addOrRemoveView(it.isVisible, actionPopup) { ActionPopup(context) }
            actionPopup?.set(it.id, it.value)
        }
    }
}