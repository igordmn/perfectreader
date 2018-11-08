package com.dmi.perfectreader.reader

import android.view.Gravity
import android.widget.FrameLayout
import com.dmi.perfectreader.action.performingActionView
import com.dmi.perfectreader.book.bookView
import com.dmi.perfectreader.control.controlView
import com.dmi.perfectreader.menu.Menu
import com.dmi.perfectreader.menu.menuView
import com.dmi.perfectreader.search.SearchUI
import com.dmi.perfectreader.search.searchUIView
import com.dmi.perfectreader.selection.selectionView
import com.dmi.perfectreader.settingsui.SettingsUI
import com.dmi.perfectreader.settingsui.settingsUIView
import com.dmi.perfectreader.tableofcontentsui.TableOfContentsUI
import com.dmi.perfectreader.tableofcontentsui.tableOfContentsUIView
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

fun ViewBuild.readerView(model: Reader) = FrameLayout(context).apply {
    val bookView = bookView(model)

    fun ViewBuild.popupView(popup: Any) = when (popup) {
        is Menu -> menuView(popup)
        is SettingsUI -> settingsUIView(popup, bookView.glContext)
        is TableOfContentsUI -> tableOfContentsUIView(popup)
        is SearchUI -> searchUIView(popup)
        else -> unsupported()
    }
    child(params(matchParent, matchParent), bookView)
    child(params(matchParent, matchParent), controlView(context, model.control))
    bindChild(params(matchParent, matchParent), model::selection, ViewBuild::selectionView).apply {
        id = generateId()
        layoutTransition = fadeTransition(300)
    }
    bindChild(params(matchParent, matchParent), model::popup, ViewBuild::popupView).apply {
        id = generateId()
        layoutTransition = fadeTransition(300)
    }
    bindChild(params(wrapContent, wrapContent, Gravity.CENTER_HORIZONTAL), model::performingAction, ViewBuild::performingActionView).apply {
        id = generateId()
        layoutTransition = fadeTransition(300)
        padding = dip(48)
    }
}