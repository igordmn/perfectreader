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

fun Context.settingsChangeView(model: SettingsChange): View {
    fun space() = view(::FrameLayout) {
        backgroundColor = color(android.R.color.transparent)
        isClickable = true
        isFocusable = true
        onClick { model.back() }
    }

    fun fontSettings(navigation: SettingsNavigation) = view(::NestedScrollView) {
        child(::LinearLayoutCompat, params(matchParent, wrapContent)) {
            operator fun View.unaryPlus() = child(this, params(matchParent, wrapContent))

            orientation = LinearLayoutCompat.VERTICAL

            with(main.settings) {
                +listSetting(navigation, format::textFontFamily, fontFamilyItems(), R.string.settingsChangeFontFamily)
                +titleSetting(fontStyleView(
                        format::textFontIsBold, format::textFontIsItalic,
                        R.string.settingsChangeFontStyleBold, R.string.settingsChangeFontStyleItalic
                ), R.string.settingsChangeFontStyle)
                +floatSetting(format::textSizeDip, SettingValues.TEXT_SIZE, titleResId = R.string.settingsChangeFontSize)
                +floatSetting(format::textScaleX, SettingValues.TEXT_SCALE_X, titleResId = R.string.settingsChangeFontWidth)
                +floatSetting(format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH, titleResId = R.string.settingsChangeFontBoldness)
                +floatSetting(format::textSkewX, SettingValues.TEXT_SKEW_X, titleResId = R.string.settingsChangeFontSkew)
                +booleanSetting(format::textAntialiasing, R.string.settingsChangeFontAntialiasing)
                +booleanSetting(format::textHinting, R.string.settingsChangeFontHinting, R.string.settingsChangeFontHintingDesc)
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

    fun bottomMain(navigation: SettingsNavigation) = view(::LinearLayoutCompat) {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)
        elevation = dipFloat(8F)

        val tabLayout = child(::TabLayout, params(matchParent, wrapContent, weight = 0F)) {
            tabMode = TabLayout.MODE_SCROLLABLE
        }

        child(::ViewPager, params(matchParent, matchParent, weight = 1F)) {
            adapter = ViewPagerAdapter(
                    string(R.string.settingsChangeFont) to { fontSettings(navigation) },
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
        child(Bottom(context, ::bottomMain), params(matchParent, dip(320), weight = 0F))

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.back(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}

private class Bottom(context: Context, main: (SettingsNavigation) -> View) : FrameLayout(context), SettingsNavigation {
    private val main = child(main(this), params(matchParent, matchParent))
    private val details = child(Details(), params(matchParent, matchParent)).apply {
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

        val toolbar = child(::Toolbar, params(matchParent, wrapContent, weight = 0F)) {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            popupTheme = R.style.Theme_AppCompat_Light
            setNavigationOnClickListener {
                goBack()
            }
        }

        val container = child(FrameLayout(context), params(matchParent, matchParent, weight = 1F))
    }
}