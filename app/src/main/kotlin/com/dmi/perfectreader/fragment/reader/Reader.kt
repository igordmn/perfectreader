package com.dmi.perfectreader.fragment.reader

import android.os.Bundle
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.control.Control
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.selection.Selection
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject

class Reader(
        createBook: () -> Book,
        createControl: (Reader) -> Control,
        private val createSelection: (Reader, close: () -> Unit) -> Selection,
        private val createMenu: (Reader, close: () -> Unit) -> Menu
) : BaseViewModel() {
    val selectionObservable = BehaviorSubject<Selection?>()
    val menuObservable = BehaviorSubject<Menu?>()

    val book = initChild(createBook())
    val control = initChild(createControl(this))
    var selection: Selection?  by rxObservable(null, selectionObservable)
    var menu: Menu? by rxObservable(null, menuObservable)

    var selectionIsOpened: Boolean by saveState(false)
        private set
    var menuIsOpened: Boolean by saveState(false)
        private set

    override fun restore(state: Bundle) {
        super.restore(state)
        if (selectionIsOpened)
            selection = initChild(initSelection())
        if (menuIsOpened)
            menu = initChild(initMenu())
    }

    fun toggleSelection() {
        selection = toggleChild(selection, initSelection)
        selectionIsOpened = selection != null
    }

    fun toggleMenu() {
        menu = toggleChild(menu, initMenu)
        menuIsOpened = menu != null
    }

    private val closeSelection = {
        require(selectionIsOpened)
        toggleSelection()
    }

    private val closeMenu = {
        require(menuIsOpened)
        toggleMenu()
    }

    private val initSelection = { createSelection(this, closeSelection) }
    private val initMenu = { createMenu(this, closeMenu) }
}