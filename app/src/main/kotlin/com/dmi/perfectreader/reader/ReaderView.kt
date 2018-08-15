package com.dmi.perfectreader.reader

import android.content.Context
import android.widget.FrameLayout
import com.dmi.perfectreader.action.performingActionView
import com.dmi.perfectreader.book.bookView
import com.dmi.perfectreader.control.controlView
import com.dmi.perfectreader.menu.menuView
import com.dmi.perfectreader.selection.selectionView
import com.dmi.util.android.view.*
import org.jetbrains.anko.matchParent

fun Context.readerView(model: Reader) = view(::FrameLayout) {
    child(bookView(model), params(matchParent, matchParent))
    child(controlView(model.control), params(matchParent, matchParent))
    bindChild(model::selection, Context::selectionView, params(matchParent, matchParent))
    bindChild(model::menu, Context::menuView, params(matchParent, matchParent)) {
        layoutTransition = fadeTransition(300)
    }
    bindChild(model::performingAction, Context::performingActionView, params(matchParent, matchParent)) {
        layoutTransition = fadeTransition(300)
    }
}