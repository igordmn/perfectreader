package com.dmi.perfectreader.ui.settings

import android.view.KeyEvent
import android.view.View
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.settings.place.control.control
import com.dmi.perfectreader.ui.settings.place.font.font
import com.dmi.perfectreader.ui.settings.place.format
import com.dmi.perfectreader.ui.settings.place.screen.screen
import com.dmi.perfectreader.ui.settings.place.theme.theme
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.screen.ScreensView
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.dmi.util.screen.StateScreen
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent

fun ViewBuild.settingsUIView(model: SettingsUI, glContext: GLContext): View {
    val settings = context.main.settings

    val places = object : Places() {
        val font = font(model, settings.font)
        val format = format(settings.format)
        val theme = theme(model, settings.theme)
        val screen = screen(model, settings.screen, glContext)
        val control = control(model, settings.control)
    }
    places.finish()

    fun ViewBuild.main(): View = VerticalLayout {
        val tabLayout = TabLayout {
            tabMode = TabLayout.MODE_SCROLLABLE
        } into container(matchParent, wrapContent, weight = 0F)

        ViewPagerSaveable {
            id = generateId()
            adapter = ViewPagerAdapter(
                    string(R.string.settingsUIFont) to places.font.viewRef,
                    string(R.string.settingsUIFormat) to places.format.viewRef,
                    string(R.string.settingsUITheme) to places.theme.viewRef,
                    string(R.string.settingsUIScreen) to places.screen.viewRef,
                    string(R.string.settingsUIControl) to places.control.viewRef
            )
            tabLayout.setupWithViewPager(this)
        } into container(matchParent, matchParent, weight = 1F)
    }

    fun ViewBuild.screenView(screen: Screen): View {
        val state = (screen as StateScreen).state
        return when (state) {
            is SettingsUIMainState -> main()
            is Id -> places[state].view(this)
            else -> unsupported(state)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun ViewBuild.popupView(popup: Any): View {
        return when (popup) {
            is Id -> places[popup].view(this)
            else -> unsupported(popup)
        }
    }

    fun space() = FrameLayout {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    return VerticalLayoutExt {
        space() into container(matchParent, 0, weight = 0.4F)
        ScreensView(context, model.screens, ViewBuild::screenView, fadeTransition(300), fadeTransition(300)).apply {
            id = generateId()
            backgroundColor = color(R.color.background)
            elevation = dipFloat(8F)
        } into container(matchParent, 0, weight = 0.6F)

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.screens.goBackward(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }.withPopup(this, model::popup, ViewBuild::popupView)
}