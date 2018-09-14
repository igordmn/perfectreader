package com.dmi.perfectreader.reader

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.dmi.perfectreader.action.performingActionView
import com.dmi.perfectreader.book.bookView
import com.dmi.perfectreader.control.controlView
import com.dmi.perfectreader.menu.Menu
import com.dmi.perfectreader.menu.menuView
import com.dmi.perfectreader.selection.selectionView
import com.dmi.perfectreader.settingschange.SettingsChange
import com.dmi.perfectreader.settingschange.settingsChangeView
import com.dmi.util.android.view.bindChild
import com.dmi.util.android.view.child
import com.dmi.util.android.view.fadeTransition
import com.dmi.util.android.view.params
import com.dmi.util.lang.unsupported
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

fun readerView(context: Context, model: Reader) = FrameLayout(context).apply {
    fun popupView(context: Context, popup: Any) = when (popup) {
        is Menu -> menuView(context, popup, model.book)
        is SettingsChange -> settingsChangeView(context, popup)
        else -> unsupported()
    }

    child(params(matchParent, matchParent), bookView(context, model))
    child(params(matchParent, matchParent), controlView(context, model.control))
    bindChild(params(matchParent, matchParent), model::selection, ::selectionView).apply {
        layoutTransition = fadeTransition(300)
    }
    bindChild(params(matchParent, matchParent), model::popup, ::popupView).apply {
        layoutTransition = fadeTransition(300)
    }
    bindChild(params(wrapContent, wrapContent, Gravity.CENTER_HORIZONTAL), model::performingAction, ::performingActionView).apply {
        layoutTransition = fadeTransition(300)
        padding = dip(48)
    }
}