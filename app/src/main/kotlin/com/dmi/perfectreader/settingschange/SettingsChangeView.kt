package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.*



fun Context.settingsChangeView(model: SettingsChange): View {
    fun space() = view(::FrameLayout) {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun fontSettings() = view(::LinearLayoutCompat) {
        child(settingsChangeFontView().apply {
            clipToPadding = false
            padding = dip(16)
        }, params(matchParent, wrapContent))
    }

    fun formatSettings() = view(::TextView) {
        text = "formatSettings"
    }

    fun themeSettings() = view(::TextView) {
        text = "themeSettings"
    }

    fun screenSettings() = view(::TextView) {
        text = "screenSettings"
    }

    fun controlSettings() = view(::TextView) {
        text = "controlSettings"
    }

    fun bottom() = view(::LinearLayoutCompat) {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        val tabLayout = child(::TabLayout, params(matchParent, wrapContent, weight = 0F)) {
            tabMode = TabLayout.MODE_SCROLLABLE
        }

        child(::ViewPagerExt, params(matchParent, matchParent, weight = 1F)) {
            isScrollEnabled = false
            adapter = ViewPagerAdapter(
                    string(R.string.settingsChangeFont) to ::fontSettings,
                    string(R.string.settingsChangeFormat) to ::formatSettings,
                    string(R.string.settingsChangeTheme) to ::themeSettings,
                    string(R.string.settingsChangeScreen) to ::screenSettings,
                    string(R.string.settingsChangeControl) to ::controlSettings
            )
            tabLayout.setupWithViewPager(this)
        }
    }

    return view(::LinearLayoutExt) {
        orientation = LinearLayoutCompat.VERTICAL
        dontSendTouchToParent()

        child(space(), params(matchParent, matchParent, weight = 1F))
        child(bottom(), params(matchParent, dip(320), weight = 0F))

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}