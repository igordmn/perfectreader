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
import com.dmi.perfectreader.settingschange.setting.fontFamilyItems
import com.dmi.perfectreader.settingschange.setting.fontStyleView
import com.dmi.util.android.view.*
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.*

typealias GoBack = () -> Unit
typealias GoDetails = (title: String, View) -> Unit

fun settingsChangeView(context: Context, model: SettingsChange): View {
    val main = context.main

    fun space() = FrameLayout(context).apply {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun fontSettings(navigation: SettingsNavigation) = NestedScrollView(context).apply {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            operator fun View.unaryPlus() = child(params(matchParent, wrapContent), this)

            orientation = LinearLayoutCompat.VERTICAL

            with(main.settings) {
                +listSetting(context, navigation, format::textFontFamily, fontFamilyItems(context), R.string.settingsChangeFontFamily)
                +titleSetting(context, fontStyleView(
                        context,
                        format::textFontIsBold, format::textFontIsItalic,
                        R.string.settingsChangeFontStyleBold, R.string.settingsChangeFontStyleItalic
                ), R.string.settingsChangeFontStyle)
                +floatSetting(context, format::textSizeDip, SettingValues.TEXT_SIZE, titleResId = R.string.settingsChangeFontSize)
                +floatSetting(context, format::textScaleX, SettingValues.TEXT_SCALE_X, titleResId = R.string.settingsChangeFontWidth)
                +floatSetting(context, format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH, titleResId = R.string.settingsChangeFontBoldness)
                +floatSetting(context, format::textSkewX, SettingValues.TEXT_SKEW_X, titleResId = R.string.settingsChangeFontSkew)
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

    fun bottomMain(navigation: SettingsNavigation) = LinearLayoutCompat(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        val tabLayout = child(params(matchParent, wrapContent, weight = 0F), TabLayout(context).apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        })

        child(params(matchParent, matchParent, weight = 1F), ViewPager(context).apply {
            adapter = ViewPagerAdapter(
                    string(R.string.settingsChangeFont) to { fontSettings(navigation) },
                    string(R.string.settingsChangeFormat) to ::formatSettings,
                    string(R.string.settingsChangeTheme) to ::themeSettings,
                    string(R.string.settingsChangeScreen) to ::screenSettings,
                    string(R.string.settingsChangeControl) to ::controlSettings
            )
            tabLayout.setupWithViewPager(this)
        })
    }

    return LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        dontSendTouchToParent()

        child(params(matchParent, matchParent, weight = 1F), space())
        child(params(matchParent, dip(320), weight = 0F), Bottom(context, ::bottomMain).apply {
            backgroundColor = color(R.color.background)
            elevation = dipFloat(8F)
        })

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}

private class Bottom(context: Context, main: (SettingsNavigation) -> View) : FrameLayout(context), SettingsNavigation {
    private val main = child(params(matchParent, matchParent), main(this))
    private val details = child(params(matchParent, matchParent), Details()).apply {
        visibility = View.INVISIBLE
    }

    private var currentDetails: View? = null

    init {
        layoutTransition = fadeTransition(300)
    }

    override fun goDetails(title: String, view: View) {
        main.visibility = View.INVISIBLE
        details.visibility = View.VISIBLE
        details.toolbar.title = title
        if (currentDetails != null)
            details.removeView(currentDetails)
        details.addView(view)
        currentDetails = view
    }

    override fun goBack() {
        main.visibility = View.VISIBLE
        details.visibility = View.INVISIBLE
        details.toolbar.title = ""
        if (currentDetails != null)
            details.removeView(currentDetails)
        currentDetails = null
    }

    inner class Details : LinearLayoutCompat(context) {
        init {
            orientation = LinearLayoutCompat.VERTICAL
            backgroundColor = color(R.color.background)
        }

        val toolbar = child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            popupTheme = R.style.Theme_AppCompat_Light
            setNavigationOnClickListener {
                goBack()
            }
        })

        val container = child(params(matchParent, matchParent, weight = 1F), FrameLayout(context))
    }
}