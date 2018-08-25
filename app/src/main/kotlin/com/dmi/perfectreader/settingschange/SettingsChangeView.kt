package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
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

    fun fontSettings() = view(::NestedScrollView) {
        child(::LinearLayoutCompat, params(matchParent, wrapContent)) {
            operator fun View.unaryPlus() = child(this, params(matchParent, wrapContent))

            orientation = LinearLayoutCompat.VERTICAL

            +floatSetting(R.string.settingsChangeFontFamily, main.settings.format::textSizeDip, SettingValues.TEXT_SIZE)
            +floatSetting(R.string.settingsChangeFontStyle, main.settings.format::textSizeDip, SettingValues.TEXT_SIZE)
            +floatSetting(R.string.settingsChangeFontSize, main.settings.format::textSizeDip, SettingValues.TEXT_SIZE)
            +floatSetting(R.string.settingsChangeFontWidth, main.settings.format::textScaleX, SettingValues.TEXT_SCALE_X)
            +floatSetting(R.string.settingsChangeFontBoldness, main.settings.format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH)
            +floatSetting(R.string.settingsChangeFontSkew, main.settings.format::textSkewX, SettingValues.TEXT_SKEWX)
            +booleanSetting(R.string.settingsChangeFontHinting, main.settings.format::textHinting)
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

        child(::ViewPager, params(matchParent, matchParent, weight = 1F)) {
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