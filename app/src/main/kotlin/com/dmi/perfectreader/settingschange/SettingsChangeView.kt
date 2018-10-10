package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.content.obj.param.TextAlign
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.common.*
import com.dmi.perfectreader.settingschange.custom.*
import com.dmi.util.android.screen.ScreensView
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.dmi.util.screen.StateScreen
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.*

fun settingsChangeView(context: Context, model: SettingsChange): View {
    val settings = context.main.settings
    val settingsExt = object {
        val format = object {
            var padding: Float
                get() = settings.format.pagePaddingLeftDip
                set(value) {
                    settings.format.pagePaddingLeftDip = value
                    settings.format.pagePaddingRightDip = value
                    settings.format.pagePaddingTopDip = value
                    settings.format.pagePaddingBottomDip = value
                }

            var justify: Boolean
                get() = settings.format.textAlign == TextAlign.JUSTIFY
                set(value) {
                    settings.format.textAlign = if (value) TextAlign.JUSTIFY else TextAlign.LEFT
                }
        }
    }

    fun vertical(vararg list: View) = NestedScrollView(context).apply {
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            list.forEach {
                child(params(matchParent, wrapContent), it)
            }
        })
    }

    val sections = object : SettingSections() {
        val font = object : Section(R.string.settingsChangeFont) {
            val family = object : Section(R.string.settingsChangeFontFamily) {
                override fun view() = fontFamilyDetails(context, model)
            }

            override fun view() = vertical(
                    detailsSetting(context, model, FontFamilyPreviewView(context), family),
                    titleSetting(context, fontStyleView(
                            context,
                            settings.format::textFontIsBold, settings.format::textFontIsItalic,
                            R.string.settingsChangeFontStyleBold, R.string.settingsChangeFontStyleItalic
                    ), R.string.settingsChangeFontStyle),
                    floatSetting(context, settings.format::textSizeDip, SettingValues.TEXT_SIZE, R.string.settingsChangeFontSize),
                    floatSetting(context, settings.format::textScaleX, SettingValues.TEXT_SCALE_X, R.string.settingsChangeFontWidth),
                    floatSetting(context, settings.format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH, R.string.settingsChangeFontBoldness),
                    floatSetting(context, settings.format::textSkewX, SettingValues.TEXT_SKEW_X, R.string.settingsChangeFontSkew),
                    booleanSetting(context, settings.format::textAntialiasing, R.string.settingsChangeFontAntialiasing),
                    booleanSetting(context, settings.format::textHinting, R.string.settingsChangeFontHinting, R.string.settingsChangeFontHintingDesc)
            )
        }

        val format = object : Section(R.string.settingsChangeFormat) {
            override fun view() = vertical(
                    floatSetting(context, settingsExt.format::padding, SettingValues.PARAGRAPH_PADDING, R.string.settingsChangeFormatPadding),
                    floatSetting(context, settings.format::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER, R.string.settingsChangeFormatLineHeight),
                    floatSetting(context, settings.format::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING, R.string.settingsChangeFormatLetterSpacing),
                    floatSetting(context, settings.format::paragraphVerticalMarginEm, SettingValues.PARAGRAPH_VERTICAL_MARGIN, R.string.settingsChangeFormatParagraphSpacing),
                    floatSetting(context, settings.format::firstLineIndentEm, SettingValues.FIRST_LINE_INDENT, R.string.settingsChangeFormatFirstLineIndent),
                    booleanSetting(context, settings.format::hyphenation, R.string.settingsChangeFormatHyphenation),
                    booleanSetting(context, settings.format::hangingPunctuation, R.string.settingsChangeFormatHangingPunctuation, R.string.settingsChangeFormatHangingPunctuationDesc),
                    booleanSetting(context, settingsExt.format::justify, R.string.settingsChangeFormatJustify)
            )
        }

        val theme = object : Section(R.string.settingsChangeTheme) {
            override fun view() = vertical()
        }

        val screen = object : Section(R.string.settingsChangeScreen) {
            val animation = object : Section(R.string.settingsChangeScreenAnimation) {
                override fun view() = screenAnimationDetails(context, model.reader.book)
            }

            override fun view() = vertical(
                    detailsSetting(context, model, ScreenAnimationPreviewView(context), animation)
            )
        }

        val control = object : Section(R.string.settingsChangeControl) {
            override fun view() = vertical()
        }
    }

    fun main(): View = LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        val tabLayout = child(params(matchParent, wrapContent, weight = 0F), TabLayout(context).apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        })

        child(params(matchParent, matchParent, weight = 1F), ViewPager(context).apply {
            adapter = ViewPagerAdapter(
                    string(sections.font.nameRes) to sections.font::view,
                    string(sections.format.nameRes) to sections.format::view,
                    string(sections.theme.nameRes) to sections.theme::view,
                    string(sections.screen.nameRes) to sections.screen::view,
                    string(sections.control.nameRes) to sections.control::view
            )
            tabLayout.setupWithViewPager(this)
        })
    }

    fun details(section: SettingSections.Section) = LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL
        backgroundColor = color(R.color.background)

        child(params(matchParent, wrapContent, weight = 0F), Toolbar(context).apply {
            setTitleTextAppearance(context, R.style.TextAppearance_MaterialComponents_Headline6)
            backgroundColor = color(android.R.color.transparent)
            navigationIcon = drawable(R.drawable.ic_arrow_back)
            this.title = string(section.nameRes)
            popupTheme = R.style.Theme_AppCompat_Light

            setNavigationOnClickListener {
                model.screens.goBackward()
            }
        })

        child(params(matchParent, matchParent, weight = 1F), section.view())
    }

    fun screenView(screen: Screen): View {
        val state = (screen as StateScreen).state
        return when (state) {
            is SettingsChangeMainState -> main()
            is SettingsChangeDetailsState -> details(sections[state.id])
            else -> unsupported(state)
        }
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

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.screens.goBackward(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }
}