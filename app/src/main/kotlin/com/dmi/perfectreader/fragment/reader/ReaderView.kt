package com.dmi.perfectreader.fragment.reader

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.view.ViewGroup
import com.dmi.perfectreader.R
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.BookView
import com.dmi.perfectreader.fragment.bookcontrol.BookControl
import com.dmi.perfectreader.fragment.bookcontrol.BookControlView
import com.dmi.perfectreader.fragment.menu.Menu
import com.dmi.perfectreader.fragment.menu.MenuView
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.find

class ReaderView(
        private val context: Context,
        private val model: Reader,
        private val createBook: (Book) -> BookView,
        private val createBookControl: (BookControl) -> BookControlView,
        private val createMenu: (Menu) -> MenuView
) : BaseView(context, R.layout.fragment_reader) {
    private val menuContainer = find<ViewGroup>(R.id.menuContainer)
    private var menu: MenuView? = null

    init {
        addChild(createBook(model.book), R.id.bookContainer)
        addChild(createBookControl(model.control), R.id.bookControlContainer)

        menuContainer.layoutTransition = LayoutTransition().apply {
            val appearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 0F, 1F);
            val disappearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 1F, 0F);

            setAnimator(LayoutTransition.APPEARING, appearingAnimation);
            setDuration(LayoutTransition.APPEARING, 300)
            setStartDelay(LayoutTransition.APPEARING, 0)

            setAnimator(LayoutTransition.DISAPPEARING, disappearingAnimation);
            setDuration(LayoutTransition.DISAPPEARING, 300)
            setStartDelay(LayoutTransition.DISAPPEARING, 0)
        }

        subscribe(model.menuObservable) { it ->
            menu = toggleChildByModel(it, menu, R.id.menuContainer) {
                createMenu(it)
            }
        }
    }
}