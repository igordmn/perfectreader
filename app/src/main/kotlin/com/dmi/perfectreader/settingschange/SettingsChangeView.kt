package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.ViewPager
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

    fun textSettings() = view(::TextView) {
        text = "textSettings"
    }

    fun viewSettings() = view(::TextView) {
        text = "viewSettings"
    }

    fun bottom() = view(::LinearLayoutCompat) {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        val tabLayout = child(TabLayout(context), params(matchParent, wrapContent, weight = 0F))
        child(::ViewPager, params(matchParent, matchParent, weight = 1F)) {
            tabLayout.setupWithViewPager(this)
            adapter = ViewPagerAdapter(
                    string(R.string.settingsChangeText) to ::textSettings,
                    string(R.string.settingsChangeView) to ::viewSettings
            )
        }
    }

    return view(::LinearLayoutExt) {
        orientation = LinearLayoutCompat.VERTICAL
        dontSendTouchToParent()

        child(space(), params(matchParent, matchParent, weight = 1F))
        child(bottom(), params(matchParent, dip(300), weight = 0F))

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}