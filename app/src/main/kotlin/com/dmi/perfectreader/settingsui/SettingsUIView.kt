package com.dmi.perfectreader.settingsui

import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settings.*
import com.dmi.perfectreader.settingsui.common.*
import com.dmi.perfectreader.settingsui.custom.*
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.screen.ScreensView
import com.dmi.util.android.screen.withPopup
import com.dmi.util.android.view.*
import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.dmi.util.screen.StateScreen
import com.dmi.util.system.Nanos
import com.dmi.util.system.minutes
import com.dmi.util.system.toMinutes
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.*
import kotlin.reflect.KMutableProperty0

fun ViewBuild.settingsUIView(model: SettingsUI, glContext: GLContext): View {
    val settings = context.main.settings

    fun ViewBuild.vertical(vararg list: View) = NestedScrollView(context).apply {
        id = generateId()
        child(params(matchParent, wrapContent), LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            list.forEach {
                child(params(matchParent, wrapContent), it)
            }
        })
    }

    infix fun View.visibleIf(condition: () -> Boolean): View {
        autorun {
            isVisible = condition()
        }
        return this
    }

    val places = object : Places() {
        fun colorPlace(property: KMutableProperty0<Int>, @StringRes titleRes: Int) = object : Place() {
            val hex = object : Place() {
                override fun ViewBuild.view() = DialogView(context) {
                    colorHEXDialog(context, model, property)
                }
            }

            override fun ViewBuild.view() = colorDetails(
                    context, model, titleRes, property, hex
            )
        }

        val font = object : Place() {
            val family = object : Place() {
                override fun ViewBuild.view() = fontFamilyDetails(context, model)
            }

            override fun ViewBuild.view() = vertical(
                    detailsSetting(context, model, fontFamilyPreview(context), family, R.string.settingsUIFontFamily),
                    titleSetting(context, fontStyleView(
                            context,
                            settings.format::textFontIsBold, settings.format::textFontIsItalic,
                            R.string.settingsUIFontStyleBold, R.string.settingsUIFontStyleItalic
                    ), R.string.settingsUIFontStyle),
                    floatSetting(context, settings.format::textSizeDip, SettingValues.TEXT_SIZE, R.string.settingsUIFontSize),
                    floatSetting(context, settings.format::textScaleX, SettingValues.TEXT_SCALE_X, R.string.settingsUIFontWidth),
                    floatSetting(context, settings.format::textStrokeWidthDip, SettingValues.TEXT_STROKE_WIDTH, R.string.settingsUIFontBoldness),
                    floatSetting(context, settings.format::textSkewX, SettingValues.TEXT_SKEW_X, R.string.settingsUIFontSkew),
                    booleanSetting(context, settings.format::textAntialiasing, R.string.settingsUIFontAntialiasing),
                    booleanSetting(context, settings.format::textHinting, R.string.settingsUIFontHinting, R.string.settingsUIFontHintingDesc)
            )
        }

        val format = object : Place() {
            override fun ViewBuild.view() = vertical(
                    floatSetting(context, settings.format::pagePadding, SettingValues.PARAGRAPH_PADDING, R.string.settingsUIFormatPadding),
                    floatSetting(context, settings.format::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER, R.string.settingsUIFormatLineHeight),
                    floatSetting(context, settings.format::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING, R.string.settingsUIFormatLetterSpacing),
                    floatSetting(context, settings.format::paragraphVerticalMarginEm, SettingValues.PARAGRAPH_VERTICAL_MARGIN, R.string.settingsUIFormatParagraphSpacing),
                    floatSetting(context, settings.format::paragraphFirstLineIndentEm, SettingValues.FIRST_LINE_INDENT, R.string.settingsUIFormatFirstLineIndent),
                    booleanSetting(context, settings.format::hyphenation, R.string.settingsUIFormatHyphenation),
                    booleanSetting(context, settings.format::hangingPunctuation, R.string.settingsUIFormatHangingPunctuation, R.string.settingsUIFormatHangingPunctuationDesc),
                    booleanSetting(context, settings.format::textJustify, R.string.settingsUIFormatJustify)
            )
        }

        val textShadow = object : Place() {
            val color = colorPlace(settings.format::textShadowColor, R.string.settingsUIThemeTextShadowColor)

            override fun ViewBuild.view() = details(
                    context, model, R.string.settingsUIThemeTextShadow,
                    vertical(
                            booleanSetting(context, settings.format::textShadowEnabled, R.string.settingsUIThemeTextShadowEnabled),
                            detailsSetting(
                                    context, model,
                                    colorPreview(context, settings.format::textShadowColor), color, R.string.settingsUIThemeTextShadowColor
                            ) visibleIf { settings.format.textShadowEnabled },
                            floatSetting(
                                    context, settings.format::textShadowOpacity, SettingValues.TEXT_SHADOW_OPACITY, R.string.settingsUIThemeTextShadowOpacity
                            ) visibleIf { settings.format.textShadowEnabled },
                            floatSetting(
                                    context, settings.format::textShadowAngleDegrees,
                                    SettingValues.TEXT_SHADOW_ANGLE, R.string.settingsUIThemeTextShadowAngle,
                                    ringValues = true
                            ) visibleIf { settings.format.textShadowEnabled },
                            floatSetting(
                                    context, settings.format::textShadowOffsetEm, SettingValues.TEXT_SHADOW_OFFSET, R.string.settingsUIThemeTextShadowOffset
                            ) visibleIf { settings.format.textShadowEnabled },
                            floatSetting(
                                    context, settings.format::textShadowSizeEm, SettingValues.TEXT_SHADOW_SIZE, R.string.settingsUIThemeTextShadowSize
                            ) visibleIf { settings.format.textShadowEnabled },
                            floatSetting(
                                    context, settings.format::textShadowBlurEm, SettingValues.TEXT_SHADOW_BLUR, R.string.settingsUIThemeTextShadowBlur
                            ) visibleIf { settings.format.textShadowEnabled }
                    )
            )
        }

        val theme = object : Place() {
            val backgroundIsPicture = object : Place() {
                private val values = arrayOf(false, true)
                private val names = values.map(::format).toTypedArray()

                fun format(isImage: Boolean) = when (isImage) {
                    false -> context.string(R.string.settingsUIThemeBackgroundSelectColor)
                    true -> context.string(R.string.settingsUIThemeBackgroundSelectPicture)
                }

                override fun ViewBuild.view() = DialogView(context) {
                    val current = values.indexOf(settings.format.pageBackgroundIsImage)
                    AlertDialog.Builder(context)
                            .setTitle(R.string.settingsUIThemeBackground)
                            .setSingleChoiceItems(names, current) { dialog, which ->
                                settings.format.pageBackgroundIsImage = values[which]
                                dialog.dismiss()
                            }
                            .setOnDismissListener {
                                model.popup = null
                            }
                            .create()
                }
            }

            val backgroundPicture = object : Place() {
                override fun ViewBuild.view() = themeBackgroundPictureDetails(context, model)
            }

            val backgroundColor = colorPlace(settings.format::pageBackgroundColor, R.string.settingsUIThemeBackgroundColor)
            val textColor = colorPlace(settings.format::textColor, R.string.settingsUIThemeText)
            val selectionColor = colorPlace(settings.selection::color, R.string.settingsUIThemeSelection)

            override fun ViewBuild.view() = vertical(
                    popupSetting(
                            context, model,
                            propertyPreview(context, settings.format::pageBackgroundIsImage, backgroundIsPicture::format),
                            backgroundIsPicture,
                            R.string.settingsUIThemeBackground
                    ),
                    detailsSetting(
                            context, model,
                            themeBackgroundPicturePreview(context), backgroundPicture, R.string.settingsUIThemeBackgroundPicture
                    ) visibleIf { settings.format.pageBackgroundIsImage },
                    detailsSetting(
                            context, model,
                            colorPreview(context, settings.format::pageBackgroundColor), backgroundColor, R.string.settingsUIThemeBackgroundColor
                    ) visibleIf { !settings.format.pageBackgroundIsImage },
                    booleanSetting(
                            context, settings.format::pageBackgroundContentAwareResize, R.string.settingsUIThemeBackgroundContentAwareResize
                    ) visibleIf { settings.format.pageBackgroundIsImage },
                    detailsSetting(
                            context, model,
                            colorPreview(context, settings.format::textColor), textColor, R.string.settingsUIThemeText
                    ),
                    detailsSetting(
                            context, model,
                            colorPreview(context, settings.selection::color), selectionColor, R.string.settingsUIThemeSelection
                    ),
                    detailsSetting(
                            context, model,
                            emptyPreview(context), textShadow, R.string.settingsUIThemeTextShadow
                    ),
                    floatSetting(context, settings.format::pageTextGammaCorrection, SettingValues.GAMMA_CORRECTION, R.string.settingsUIThemeTextGammaCorrection)
            )
        }

        val screen = object : Place() {
            val animation = object : Place() {
                override fun ViewBuild.view() = screenAnimationDetails(context, model, model.reader.book, glContext)
            }

            val timeout = object : Place() {
                private val values = arrayOf(-1L, minutes(1.0), minutes(2.0), minutes(5.0), minutes(10.0), minutes(30.0))
                private val names = values.map(::format).toTypedArray()

                fun format(time: Nanos) = when (time) {
                    -1L -> context.string(R.string.settingsUIScreenTimeoutSystem)
                    else -> {
                        val minutes = time.toMinutes().toInt()
                        context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
                    }
                }

                override fun ViewBuild.view() = DialogView(context) {
                    var current = values.indexOf(settings.screen.timeout)
                    if (current < 0)
                        current = 0
                    AlertDialog.Builder(context)
                            .setTitle(R.string.settingsUIScreenTimeout)
                            .setSingleChoiceItems(names, current) { dialog, which ->
                                settings.screen.timeout = values[which]
                                dialog.dismiss()
                            }
                            .setOnDismissListener {
                                model.popup = null
                            }
                            .create()
                }
            }

            val footerElements = object : Place() {
                private fun BooleanArray.toElements() = PageFooterElements(this[0], this[1], this[2])
                private fun PageFooterElements.toArray() = booleanArrayOf(pageNumber, numberOfPages, chapter)
                private val names = arrayOf(
                        R.string.settingsUIScreenFooterPageNumber,
                        R.string.settingsUIScreenFooterNumberOfPages,
                        R.string.settingsUIScreenFooterChapter
                ).map { context.string(it) }.toTypedArray()

                fun format(footerElements: PageFooterElements): String {
                    val count = footerElements.toArray().count { it }
                    return if (count == 0) {
                        context.string(R.string.settingsUIScreenFooterHide)
                    } else {
                        context.resources.getQuantityString(R.plurals.settingsUIScreenFooterCount, count, count)
                    }
                }

                override fun ViewBuild.view() = DialogView(context) {
                    val checked = settings.format.pageFooterElements.toArray()
                    AlertDialog.Builder(context)
                            .setTitle(R.string.settingsUIScreenFooter)
                            .setPositiveButton(android.R.string.ok) { _, _ -> }
                            .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                                checked[which] = isChecked
                            }
                            .setOnDismissListener {
                                model.popup = null
                                settings.format.pageFooterElements = checked.toElements()
                            }
                            .create()
                }
            }

            override fun ViewBuild.view() = vertical(
                    detailsSetting(context, model, screenAnimationPreview(context, glContext), animation, R.string.settingsUIScreenAnimation),
                    popupSetting(
                            context, model,
                            propertyPreview(context, settings.screen::timeout, timeout::format),
                            timeout,
                            R.string.settingsUIScreenTimeout
                    ),
                    popupSetting(
                            context, model,
                            propertyPreview(context, settings.format::pageFooterElements, footerElements::format),
                            footerElements,
                            R.string.settingsUIScreenFooter
                    )
            )
        }

        val control = object : Place() {
            override fun ViewBuild.view() = vertical()
        }
    }

    fun ViewBuild.main(): View = LinearLayoutExt(context).apply {
        orientation = LinearLayoutCompat.VERTICAL

        val tabLayout = child(params(matchParent, wrapContent, weight = 0F), TabLayout(context).apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        })

        child(params(matchParent, matchParent, weight = 1F), ViewPagerSaveable(context).apply {
            id = generateId()
            adapter = ViewPagerAdapter(
                    string(R.string.settingsUIFont) to places.font.viewRef,
                    string(R.string.settingsUIFormat) to places.format.viewRef,
                    string(R.string.settingsUITheme) to places.theme.viewRef,
                    string(R.string.settingsUIScreen) to places.screen.viewRef,
                    string(R.string.settingsUIControl) to places.control.viewRef
            )
            tabLayout.setupWithViewPager(this)
        })
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
        child(params(matchParent, dip(320), weight = 0F), ScreensView(context, model.screens, ViewBuild::screenView).apply {
            id = generateId()
            backgroundColor = color(R.color.background)
            elevation = dipFloat(8F)
        })

        onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.screens.goBackward(); true }
        onInterceptKeyDown(KeyEvent.KEYCODE_MENU) { model.back(); true }
    }.withPopup(this, model::popup, ViewBuild::popupView)
}