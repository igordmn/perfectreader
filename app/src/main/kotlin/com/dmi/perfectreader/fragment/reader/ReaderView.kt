package com.dmi.perfectreader.fragment.reader

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
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

class ReaderView(
        context: Context,
        model: Reader,
        createBook: (Book) -> BookView,
        createControl: (Control) -> ControlView,
        private val createSelection: (Selection) -> SelectionView,
        private val createMenu: (Menu) -> MenuView
) : BaseView(context, R.layout.fragment_reader) {
    private val menuContainer = find<ViewGroup>(R.id.menuContainer)
    private var selection: SelectionView? = null
    private var menu: MenuView? = null

    init {
        addChild(createBook(model.book), R.id.bookContainer)
        addChild(createControl(model.control), R.id.controlContainer)

        menuContainer.layoutTransition = LayoutTransition().apply {
            val appearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 0F, 1F)
            val disappearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 1F, 0F)

            setAnimator(LayoutTransition.APPEARING, appearingAnimation)
            setDuration(LayoutTransition.APPEARING, 300)
            setStartDelay(LayoutTransition.APPEARING, 0)

            setAnimator(LayoutTransition.DISAPPEARING, disappearingAnimation)
            setDuration(LayoutTransition.DISAPPEARING, 300)
            setStartDelay(LayoutTransition.DISAPPEARING, 0)
        }

        subscribe(model.selectionObservable) { it ->
            selection = toggleChildByModel(it, selection, R.id.selectionContainer, createSelection)
        }

        subscribe(model.menuObservable) { it ->
            menu = toggleChildByModel(it, menu, R.id.menuContainer, createMenu)
        }
    }
}