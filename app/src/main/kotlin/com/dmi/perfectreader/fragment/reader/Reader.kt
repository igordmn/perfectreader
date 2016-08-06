package com.dmi.perfectreader.fragment.reader

import android.os.Bundle
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.bookcontrol.BookControl
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject

class Reader(
        createBook: () -> Book,
        createBookControl: (Reader) -> BookControl,
        private val createMenu: (Reader, close: () -> Unit) -> Menu
) : BaseViewModel() {
    val menuObservable = BehaviorSubject<Menu?>()

    val book = initChild(createBook())
    val control = initChild(createBookControl(this))
    var menu: Menu? by rxObservable(null, menuObservable)

    private var menuIsOpened: Boolean by saveState(false)

    override fun restore(state: Bundle) {
        super.restore(state)
        if (menuIsOpened)
            menu = initChild(initMenu())
    }

    fun toggleMenu() {
        menu = toggleChild(menu, initMenu)
        menuIsOpened = menu != null
    }

    private val closeMenu = {
        require(menuIsOpened)
        toggleMenu()
    }

    private val initMenu = { createMenu(this, closeMenu) }
}