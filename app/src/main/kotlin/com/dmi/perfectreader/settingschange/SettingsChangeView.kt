package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.Gravity
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
        orientation = LinearLayoutCompat.VERTICAL

        child(settingsChangeFontView().apply {
            clipToPadding = false
            setPadding(dip(16), 0, dip(16), 0)
        }, params(matchParent, wrapContent) {
            topMargin = dip(16)
        })

        child(::LinearLayoutCompat, params(matchParent, wrapContent) {
            topMargin = dip(16)
            leftMargin = dip(16)
            rightMargin = dip(16)
        }) {
            orientation = LinearLayoutCompat.VERTICAL

            child(::LinearLayoutCompat, params(wrapContent, wrapContent, gravity = Gravity.CENTER_HORIZONTAL)) {
                orientation = LinearLayoutCompat.HORIZONTAL
                child(settingsChangeFontSize(), params(wrapContent, wrapContent) {
                    rightMargin = dip(24)
                })
                child(settingsChangeFontSkewX(), params(wrapContent, wrapContent) {
                    leftMargin = dip(24)
                })
            }

            child(::LinearLayoutCompat, params(wrapContent, wrapContent, gravity = Gravity.CENTER_HORIZONTAL)) {
                orientation = LinearLayoutCompat.HORIZONTAL
                child(settingsChangeFontScaleX(), params(wrapContent, wrapContent) {
                    rightMargin = dip(24)
                })
                child(settingsChangeFontStrokeWidth(), params(wrapContent, wrapContent) {
                    leftMargin = dip(24)
                })
            }
        }
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