package com.dmi.perfectreader.ui.action

import com.dmi.perfectreader.settings.Settings
import com.dmi.perfectreader.settings.brightnessValueAndEnable
import com.dmi.perfectreader.settings.paddingDip
import com.dmi.perfectreader.settings.switchStyle
import com.dmi.perfectreader.ui.book.Book
import com.dmi.perfectreader.ui.book.page.PageScroller
import com.dmi.perfectreader.ui.reader.Reader
import com.dmi.perfectreader.ui.reader.ReaderContext
import com.dmi.perfectreader.ui.settings.SettingValues
import com.dmi.perfectreader.ui.settings.chooseSettingValue
import com.dmi.util.action.*
import com.dmi.util.graphic.PositionF
import com.dmi.util.input.TouchArea
import kotlinx.coroutines.Dispatchers
import java.lang.Math.abs
import java.lang.Math.round
import kotlin.reflect.KMutableProperty0

class Actions(
        private val context: ReaderContext,
        private val reader: Reader,
        private val density: Float = context.main.density,
        private val settings: Settings = context.main.settings
) {
    private val book: Book get() = reader.book

    operator fun get(id: ActionID): Action = when (id) {
        ActionID.NONE -> NoneAction

        ActionID.SHOW_MENU -> performAction { reader.showMenu() }
        ActionID.SHOW_LIBRARY -> performAction { reader.showLibrary() }
        ActionID.SHOW_SETTINGS -> performAction { reader.showSettings() }
        ActionID.SHOW_SEARCH -> performAction { reader.showSearch() }
        ActionID.SHOW_TABLE_OF_CONTENTS -> performAction { reader.showTableOfContents() }

        ActionID.SCROLL_PAGE -> scrollPage()
        ActionID.GO_PAGE_NEXT -> repeatAction { book.goRelative(1) }
        ActionID.GO_PAGE_PREVIOUS -> repeatAction { book.goRelative(-1) }
        ActionID.GO_PAGE_NEXT_5 -> repeatAction { book.goRelative(5) }
        ActionID.GO_PAGE_PREVIOUS_5 -> repeatAction { book.goRelative(-5) }
        ActionID.GO_PAGE_NEXT_10 -> repeatAction { book.goRelative(10) }
        ActionID.GO_PAGE_PREVIOUS_10 -> repeatAction { book.goRelative(-10) }
        ActionID.GO_PAGE_NEXT_20 -> repeatAction { book.goRelative(20) }
        ActionID.GO_PAGE_PREVIOUS_20 -> repeatAction { book.goRelative(-20) }
        ActionID.GO_PAGE_NEXT_ANIMATED -> repeatAction { book.animateRelative(1) }
        ActionID.GO_PAGE_PREVIOUS_ANIMATED -> repeatAction { book.animateRelative(-1) }
        ActionID.GO_PAGE_NEXT_5_ANIMATED -> repeatAction { book.animateRelative(5) }
        ActionID.GO_PAGE_PREVIOUS_5_ANIMATED -> repeatAction { book.animateRelative(-5) }
        ActionID.GO_PAGE_NEXT_10_ANIMATED -> repeatAction { book.animateRelative(10) }
        ActionID.GO_PAGE_PREVIOUS_10_ANIMATED -> repeatAction { book.animateRelative(-10) }
        ActionID.GO_PAGE_NEXT_20_ANIMATED -> repeatAction { book.animateRelative(20) }
        ActionID.GO_PAGE_PREVIOUS_20_ANIMATED -> repeatAction { book.animateRelative(-20) }

        ActionID.GO_CHAPTER_NEXT -> repeatAction { book.goNextChapter() }
        ActionID.GO_CHAPTER_PREVIOUS -> repeatAction { book.goPreviousChapter() }
        ActionID.GO_BOOK_BEGIN -> repeatAction { book.goBegin() }
        ActionID.GO_BOOK_END -> repeatAction { book.goEnd() }

        ActionID.WORD_SELECT -> touchAction { reader.select(book.selections?.at(it.position)) }

        ActionID.SETTINGS_FONT_SIZE_CHANGE -> ChangeSetting(NumberSettingActionID.FONT_SIZE, settings.font::sizeDip, SettingValues.FONT_SIZE_DIP)
        ActionID.SETTINGS_FONT_WIDTH_CHANGE -> ChangeSetting(NumberSettingActionID.FONT_WIDTH, settings.font::scaleX, SettingValues.FONT_WIDTH)
        ActionID.SETTINGS_FONT_BOLDNESS_CHANGE -> ChangeSetting(NumberSettingActionID.FONT_BOLDNESS, settings.font::strokeWidthEm, SettingValues.FONT_BOLDNESS_EM)
        ActionID.SETTINGS_FONT_SKEW_CHANGE -> ChangeSetting(NumberSettingActionID.FONT_SKEW, settings.font::skewX, SettingValues.FONT_SKEW)
        ActionID.SETTINGS_FORMAT_PADDING_CHANGE -> ChangeSetting(NumberSettingActionID.FORMAT_PADDING, settings.format::paddingDip, SettingValues.FORMAT_PADDING)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_CHANGE -> ChangeSetting(NumberSettingActionID.FORMAT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.FORMAT_LINE_HEIGHT_MULTIPLIER)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_CHANGE -> ChangeSetting(NumberSettingActionID.FORMAT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.FONT_LETTER_SPACING_EM)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_CHANGE -> ChangeSetting(NumberSettingActionID.FORMAT_PARAGRAPH_SPACING, settings.format::paragraphVerticalMarginEm, SettingValues.FORMAT_PARAGRAPH_VERTICAL_MARGIN_EM)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_CHANGE -> ChangeSetting(NumberSettingActionID.FORMAT_FIRST_LINE_INDENT, settings.format::paragraphFirstLineIndentEm, SettingValues.FORMAT_FIRST_LINE_INDENT_EM)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_CHANGE -> ChangeSetting(NumberSettingActionID.SCREEN_BRIGHTNESS, settings.screen::brightnessValueAndEnable, SettingValues.SCREEN_BRIGHTNESS)

        ActionID.SETTINGS_FONT_SIZE_INCREASE -> IncreaseSetting(NumberSettingActionID.FONT_SIZE, settings.font::sizeDip, SettingValues.FONT_SIZE_DIP)
        ActionID.SETTINGS_FONT_WIDTH_INCREASE -> IncreaseSetting(NumberSettingActionID.FONT_WIDTH, settings.font::scaleX, SettingValues.FONT_WIDTH)
        ActionID.SETTINGS_FONT_BOLDNESS_INCREASE -> IncreaseSetting(NumberSettingActionID.FONT_BOLDNESS, settings.font::strokeWidthEm, SettingValues.FONT_BOLDNESS_EM)
        ActionID.SETTINGS_FONT_SKEW_INCREASE -> IncreaseSetting(NumberSettingActionID.FONT_SKEW, settings.font::skewX, SettingValues.FONT_SKEW)
        ActionID.SETTINGS_FORMAT_PADDING_INCREASE -> IncreaseSetting(NumberSettingActionID.FORMAT_PADDING, settings.format::paddingDip, SettingValues.FORMAT_PADDING)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_INCREASE -> IncreaseSetting(NumberSettingActionID.FORMAT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.FORMAT_LINE_HEIGHT_MULTIPLIER)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_INCREASE -> IncreaseSetting(NumberSettingActionID.FORMAT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.FONT_LETTER_SPACING_EM)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_INCREASE -> IncreaseSetting(NumberSettingActionID.FORMAT_PARAGRAPH_SPACING, settings.format::paragraphVerticalMarginEm, SettingValues.FORMAT_PARAGRAPH_VERTICAL_MARGIN_EM)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_INCREASE -> IncreaseSetting(NumberSettingActionID.FORMAT_FIRST_LINE_INDENT, settings.format::paragraphFirstLineIndentEm, SettingValues.FORMAT_FIRST_LINE_INDENT_EM)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_INCREASE -> IncreaseSetting(NumberSettingActionID.SCREEN_BRIGHTNESS, settings.screen::brightnessValueAndEnable, SettingValues.SCREEN_BRIGHTNESS)

        ActionID.SETTINGS_FONT_SIZE_DECREASE -> DecreaseSetting(NumberSettingActionID.FONT_SIZE, settings.font::sizeDip, SettingValues.FONT_SIZE_DIP)
        ActionID.SETTINGS_FONT_WIDTH_DECREASE -> DecreaseSetting(NumberSettingActionID.FONT_WIDTH, settings.font::scaleX, SettingValues.FONT_WIDTH)
        ActionID.SETTINGS_FONT_BOLDNESS_DECREASE -> DecreaseSetting(NumberSettingActionID.FONT_BOLDNESS, settings.font::strokeWidthEm, SettingValues.FONT_BOLDNESS_EM)
        ActionID.SETTINGS_FONT_SKEW_DECREASE -> DecreaseSetting(NumberSettingActionID.FONT_SKEW, settings.font::skewX, SettingValues.FONT_SKEW)
        ActionID.SETTINGS_FORMAT_PADDING_DECREASE -> DecreaseSetting(NumberSettingActionID.FORMAT_PADDING, settings.format::paddingDip, SettingValues.FORMAT_PADDING)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_DECREASE -> DecreaseSetting(NumberSettingActionID.FORMAT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.FORMAT_LINE_HEIGHT_MULTIPLIER)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_DECREASE -> DecreaseSetting(NumberSettingActionID.FORMAT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.FONT_LETTER_SPACING_EM)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_DECREASE -> DecreaseSetting(NumberSettingActionID.FORMAT_PARAGRAPH_SPACING, settings.format::paragraphVerticalMarginEm, SettingValues.FORMAT_PARAGRAPH_VERTICAL_MARGIN_EM)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_DECREASE -> DecreaseSetting(NumberSettingActionID.FORMAT_FIRST_LINE_INDENT, settings.format::paragraphFirstLineIndentEm, SettingValues.FORMAT_FIRST_LINE_INDENT_EM)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_DECREASE -> DecreaseSetting(NumberSettingActionID.SCREEN_BRIGHTNESS, settings.screen::brightnessValueAndEnable, SettingValues.SCREEN_BRIGHTNESS)

        ActionID.SETTINGS_THEME_STYLE_SWITCH -> performAction { settings.switchStyle() }
        ActionID.SETTINGS_SCREEN_FOOTER_SWITCH -> performAction { settings.screen.footerEnabled = !settings.screen.footerEnabled }
    }

    private fun scrollPage() = object : Action {
        lateinit var scroller: PageScroller

        override fun startScroll(area: TouchArea) = run { scroller = book.scroll() }
        override fun scroll(delta: PositionF) = scroller.scroll(-delta)
        override fun endScroll(velocity: PositionF) = scroller.end(-velocity)
        override fun cancelScroll() = scroller.cancel()
    }

    private fun repeatAction(action: () -> Unit) = object : RepeatAction(Dispatchers.Main, periodMillis = 400) {
        override fun perform() = action()
    }

    private inner class ChangeSetting(
            private val id: NumberSettingActionID,
            private val property: KMutableProperty0<Float>,
            private val values: FloatArray
    ) : Action {
        private val sensitivity = 16F * density

        private var deltaFromLast = 0F

        override fun startChange() {
            showPopup()
            deltaFromLast = 0F
        }

        override fun change(delta: Float) {
            deltaFromLast += delta
            if (abs(deltaFromLast) >= sensitivity) {
                val indexDelta: Int = round(deltaFromLast / sensitivity)
                deltaFromLast -= indexDelta * sensitivity
                setBookSetting(values, property, indexDelta)
                showPopup()
            }
        }

        override fun endChange() {
            reader.performingAction = null
        }

        private fun showPopup() {
            reader.performingAction = PerformingAction(id, property.get())
        }
    }

    private abstract inner class DeltaSetting(
            private val id: NumberSettingActionID,
            private val property: KMutableProperty0<Float>,
            private val values: FloatArray,
            private val offset: Int
    ) : RepeatAction(Dispatchers.Main, periodMillis = 200) {
        private var popupShowed = false

        override fun perform() {
            setBookSetting(values, property, offset)
            if (popupShowed)
                showPopup()
        }

        override fun startTap() {
            super.startTap()
            popupShowed = true
            showPopup()
        }

        override fun endTap() {
            reader.performingAction = null
            popupShowed = false
            super.endTap()
        }

        private fun showPopup() {
            reader.performingAction = PerformingAction(id, property.get())
        }
    }

    private inner class IncreaseSetting(
            id: NumberSettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSetting(id, property, values, 1)

    private inner class DecreaseSetting(
            id: NumberSettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSetting(id, property, values, -1)

    private fun setBookSetting(values: FloatArray, property: KMutableProperty0<Float>, offset: Int) {
        val oldValue = property.get()
        val newValue = chooseSettingValue(values, oldValue, offset)
        if (newValue != oldValue) {
            property.set(newValue)
        }
    }
}