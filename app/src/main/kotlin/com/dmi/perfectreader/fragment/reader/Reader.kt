package com.dmi.perfectreader.fragment.reader

import android.os.Bundle
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.control.Control
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.reader.action.ReaderActions
import com.dmi.perfectreader.fragment.reader.action.ReaderSettingActionID
import com.dmi.perfectreader.fragment.selection.Selection
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject

class Reader(
        createBook: () -> Book,
        createControl: (Reader) -> Control,
        private val createSelection: (Reader) -> Selection,
        private val createMenu: (Reader, close: () -> Unit) -> Menu,
        createActions: (Reader) -> ReaderActions
) : BaseViewModel() {
    val selectionObservable = BehaviorSubject<Selection?>()
    val menuObservable = BehaviorSubject<Menu?>()
    val actionPopupObservable = BehaviorSubject<ActionPopupModel>()

    val actions = createActions(this)

    val book = initChild(createBook())
    val control = initChild(createControl(this))
    var selection: Selection?  by rxObservable(null, selectionObservable)
    var menu: Menu? by rxObservable(null, menuObservable)

    var menuIsOpened: Boolean by saveState(false)
        private set
    var actionPopup: ActionPopupModel by rxObservable(ActionPopupModel.INVISIBLE, actionPopupObservable)
        private set

    init {
        subscribe(book.isSelectedObservable) {
            selection = addOrRemoveChild(it, selection) { createSelection(this) }
        }
    }

    override fun restore(state: Bundle) {
        super.restore(state)
        if (menuIsOpened)
            menu = initChild(initMenu())
    }

    fun toggleMenu() {
        menu = toggleChild(menu, initMenu)
        menuIsOpened = menu != null
    }

    fun showActionPopup(id: ReaderSettingActionID, value: Any) {
        actionPopup = ActionPopupModel.visible(id, value)
    }

    fun hideActionPopup() {
        actionPopup = ActionPopupModel.INVISIBLE
    }

    private val closeMenu = {
        require(menuIsOpened)  // todo срабатывает exception
        toggleMenu()
    }

    private val initMenu = { createMenu(this, closeMenu) }

    class ActionPopupModel(val isVisible: Boolean, val id: ReaderSettingActionID, val value: Any) {
        companion object {
            val INVISIBLE = ActionPopupModel(isVisible = false, id = ReaderSettingActionID.NONE, value = "")
            fun visible(id: ReaderSettingActionID, value: Any) = ActionPopupModel(true, id, value)
        }
    }
}