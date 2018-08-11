package com.dmi.perfectreader.reader

import android.view.ViewGroup
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ViewContext
import com.dmi.perfectreader.book.BookView
import com.dmi.perfectreader.control.ControlView
import com.dmi.perfectreader.menu.MenuView
import com.dmi.perfectreader.selection.SelectionView
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.find
import com.dmi.util.android.ext.addOrRemoveView
import com.dmi.util.android.widget.fadeTransition

class ReaderView(
        private val viewContext: ViewContext,
        model: Reader
) : BaseView(viewContext.android, R.layout.fragment_reader) {
    private val menuContainer = find<ViewGroup>(R.id.menuContainer)
    private val actionPopupContainer = find<ViewGroup>(R.id.actionPopupContainer)
    private var selection: SelectionView? = null
    private var menu: MenuView? = null
    private var actionPopup: ActionPopup? = null

    init {
        addChild(BookView(viewContext, model), R.id.bookContainer)
        addChild(ControlView(viewContext, model.control), R.id.controlContainer)

        menuContainer.layoutTransition = fadeTransition(300)
        actionPopupContainer.layoutTransition = fadeTransition(300)

        autorun {
            selection = toggleChildByModel(model.selection, selection, R.id.selectionContainer) { SelectionView(viewContext, it) }
        }

        autorun {
            menu = toggleChildByModel(model.menu, menu, R.id.menuContainer) { MenuView(viewContext, it) }
        }

        autorun {
            actionPopup = actionPopupContainer.addOrRemoveView(model.actionPopup.isVisible, actionPopup) { ActionPopup(context) }
            actionPopup?.set(model.actionPopup.id, model.actionPopup.value)
        }
    }
}