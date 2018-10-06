package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.custom.fontStyleView
import com.dmi.perfectreader.settingschange.detail.FontFamilyViews
import com.dmi.perfectreader.settingschange.detail.settingViewDetails
import com.dmi.util.android.screen.ScreensView
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.*

fun settingsChangeView(context: Context, model: SettingsChange): View {
    fun screenView(screen: Screen): View = when (screen) {
        is SettingsChangeMain -> settingChangeMainView(context, screen)
        is SettingsChangeDetails -> settingChangeDetailsView(context, screen)
        else -> unsupported()
    }

    fun space() = FrameLayout(context).apply {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    return LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        dontSendTouchToParent()

        child(params(matchParent, matchParent, weight = 1F), space())
        child(params(matchParent, dip(320), weight = 0F), ScreensView(context, model.screens, ::screenView).apply {
            backgroundColor = color(R.color.background)
            elevation = dipFloat(8F)
        })

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}

fun settingChangeMainView(context: Context, model: SettingsChangeMain): View {
    val settings = context.main.settings

    fun fontSettings() = NestedScrollView(context).apply {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            operator fun View.unaryPlus() = child(params(matchParent, wrapContent), this)

            orientation = LinearLayoutCompat.VERTICAL

            with(settings) {
                +detailSetting(context, model, FontFamilyViews)
                +titleSetting(context, fontStyleView(
                        context,
                        format::textFontIsBold, format::textFontIsItalic,
                        R.string.settingsChangeFontStyleBold, R.string.settingsChangeFontStyleItalic
                ), R.string.settingsChangeFontStyle)
                +floatSetting(context, format::textSizeDip, SettingValues.TEXT_SIZE, R.string.settingsChangeFontSize)
                +floatSetting(context, format::textScaleX, SettingValues.TEXT_SCALE_X, R.string.settingsChangeFontWidth)
                +floatSetting(context, format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH, R.string.settingsChangeFontBoldness)
                +floatSetting(context, format::textSkewX, SettingValues.TEXT_SKEW_X, R.string.settingsChangeFontSkew)
                +booleanSetting(context, format::textAntialiasing, R.string.settingsChangeFontAntialiasing)
                +booleanSetting(context, format::textHinting, R.string.settingsChangeFontHinting, R.string.settingsChangeFontHintingDesc)
            }
        })
    }

    fun formatSettings() = TextView(context).apply {
        text = "formatSettings"
    }

    fun themeSettings() = TextView(context).apply {
        text = "themeSettings"
    }

    fun screenSettings() = TextView(context).apply {
        text = "screenSettings"
    }

    fun controlSettings() = TextView(context).apply {
        text = "controlSettings"
    }

    return LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        val tabLayout = child(params(matchParent, wrapContent, weight = 0F), TabLayout(context).apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        })

        child(params(matchParent, matchParent, weight = 1F), ViewPager(context).apply {
            adapter = ViewPagerAdapter(
                    string(R.string.settingsChangeFont) to ::fontSettings,
                    string(R.string.settingsChangeFormat) to ::formatSettings,
                    string(R.string.settingsChangeTheme) to ::themeSettings,
                    string(R.string.settingsChangeScreen) to ::screenSettings,
                    string(R.string.settingsChangeControl) to ::controlSettings
            )
            tabLayout.setupWithViewPager(this)
        })

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
    }
}

fun settingChangeDetailsView(context: Context, model: SettingsChangeDetails) = LinearLayoutExt(context).apply {
    val details = settingViewDetails(model.content)

    orientation = LinearLayoutCompat.VERTICAL
    backgroundColor = color(R.color.background)

    child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
        setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
        backgroundColor = color(android.R.color.transparent)
        navigationIcon = drawable(R.drawable.ic_arrow_back)
        this.title = string(details.titleResId)
        popupTheme = R.style.Theme_AppCompat_Light

        setNavigationOnClickListener {
            model.back()
        }
    })

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }

    child(params(matchParent, matchParent, weight = 1F), details.contentView(context))
}