package com.dmi.perfectreader.ui.reader

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.action.performingActionView
import com.dmi.perfectreader.ui.book.bookView
import com.dmi.perfectreader.ui.control.controlView
import com.dmi.perfectreader.ui.menu.Menu
import com.dmi.perfectreader.ui.menu.menuView
import com.dmi.perfectreader.ui.search.SearchUI
import com.dmi.perfectreader.ui.search.searchUIView
import com.dmi.perfectreader.ui.selection.selectionView
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.settingsUIView
import com.dmi.perfectreader.ui.tableofcontents.TableOfContentsUI
import com.dmi.perfectreader.ui.tableofcontents.tableOfContentsUIView
import com.dmi.util.android.system.screenBrighness
import com.dmi.util.android.system.screenTimeout
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
        fitsSystemWindows = true
    }
    bindChild(params(wrapContent, wrapContent, Gravity.CENTER_HORIZONTAL), model::performingAction, ViewBuild::performingActionView).apply {
        id = generateId()
        layoutTransition = fadeTransition(300)
        padding = dip(48)
        fitsSystemWindows = true
    }

    applyTimeout(model)
    applyBrightness(model)
    applyFullscreen(model)

    isClickable = true
    isFocusableInTouchMode = true
}

private fun View.applyTimeout(model: Reader) {
    val activity = context as ActivityExt<*>

    autorun {
        activity.screenTimeout = if (model.popup == null) context.main.settings.screen.timeout else -1
    }
}

private fun View.applyBrightness(model: Reader) {
    val activity = context as Activity

    autorun {
        val isSystem = context.main.settings.screen.brightnessIsSystem
        val brightness = context.main.settings.screen.brightnessValue
        val popup = model.popup
        val needApply = popup == null || (popup is SettingsUI && popup.applyScreenBrightness)
        val activityBrightness = if (isSystem) -1F else brightness
        activity.screenBrighness = if (needApply) activityBrightness else -1F
    }
}

private fun View.applyFullscreen(model: Reader) {
    val activity = context as ActivityExt<*>
    val original = activity.window.decorView.systemUiVisibility

    fun Activity.hideSystemUI() {
        window.decorView.systemUiVisibility = (original or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun Activity.showSystemUI() {
        window.decorView.systemUiVisibility = original
    }

    autorun {
        if (model.popup == null) {
            activity.hideSystemUI()
        } else {
            activity.showSystemUI()
        }
    }

    activity.onWindowFocusChanged { hasFocus ->
        if (hasFocus && model.popup == null)
            activity.hideSystemUI()
    }

    setOnSystemUiVisibilityChangeListener { visibility ->
        val isFullscreen = visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0
        if (activity.hasWindowFocus() && isFullscreen && model.popup == null) {
            model.showMenu()
        }
    }
}