package com.dmi.perfectreader.action

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.page.PageScroller
import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.reader.ReaderContext
import com.dmi.perfectreader.settings.Settings
import com.dmi.perfectreader.settingsui.SettingValues
import com.dmi.perfectreader.settingsui.chooseSettingValue
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
        ActionID.GO_TO_LIBRARY_LAST -> NoneAction
        ActionID.GO_TO_LIBRARY_FAVOURITE -> NoneAction
        ActionID.GO_TO_LIBRARY_FILES -> NoneAction
        ActionID.GO_TO_LIBRARY_OPDS -> NoneAction
        ActionID.GO_TO_SETTINGS -> NoneAction
        ActionID.GO_TO_TABLE_OF_CONTENTS -> NoneAction
        ActionID.GO_TO_BOOKMARKS -> NoneAction
        ActionID.GO_TO_NOTES -> NoneAction
        ActionID.GO_TO_MARKS -> NoneAction
        ActionID.GO_TO_NEXT_BOOK_IN_HISTORY -> NoneAction
        ActionID.GO_TO_PREVIOUS_BOOK_IN_HISTORY -> NoneAction
        ActionID.SHOW_FAST_SETTINGS -> NoneAction
        ActionID.SHOW_SEARCH -> NoneAction
        ActionID.SHOW_GO_PAGE -> NoneAction
        ActionID.SHOW_BOOK_INFO -> NoneAction
        ActionID.TOGGLE_AUTOSCROLL -> NoneAction
        ActionID.TOGGLE_TEXT_SPEECH -> NoneAction
        ActionID.ADD_BOOKMARK -> NoneAction
        ActionID.ADD_BOOK_TO_FAVOURITE -> NoneAction

        ActionID.SCROLL -> object : Action {
            lateinit var scroller: PageScroller

            override fun startScroll(area: TouchArea) = run { scroller = book.scroll() }
            override fun scroll(delta: PositionF) = scroller.scroll(-delta)
            override fun endScroll(velocity: PositionF) = scroller.end(-velocity)
            override fun cancelScroll() = scroller.cancel()
        }
        ActionID.GO_NEXT_PAGE -> repeatAction { book.animateRelative(1) }
        ActionID.GO_PREVIOUS_PAGE -> repeatAction { book.animateRelative(-1) }
        ActionID.GO_NEXT_PAGE_WITHOUT_ANIMATION -> repeatAction { book.goRelative(1) }
        ActionID.GO_PREVIOUS_PAGE_WITHOUT_ANIMATION -> repeatAction { book.goRelative(-1) }
        ActionID.GO_NEXT_PAGE_10 -> performAction { book.animateRelative(10) }
        ActionID.GO_PREVIOUS_PAGE_10 -> performAction { book.animateRelative(-10) }
        ActionID.GO_BOOK_BEGIN -> NoneAction
        ActionID.GO_BOOK_END -> NoneAction
        ActionID.GO_NEXT_CHAPTER -> NoneAction
        ActionID.GO_PREVIOUS_CHAPTER -> NoneAction
        ActionID.GO_BACK_BY_HISTORY -> NoneAction
        ActionID.GO_FORWARD_BY_HISTORY -> NoneAction

        ActionID.SELECT_WORD -> touchAction { reader.select(book.selections?.at(it.position)) }
        ActionID.SELECT_WORD_AT_CENTER -> performAction { reader.select(book.selections?.center()) }
        ActionID.TRANSLATE_WORD -> NoneAction
        ActionID.SEARCH_WORD -> NoneAction
        ActionID.WIKI_WORD -> NoneAction

        ActionID.NEXT_THEME -> NoneAction
        ActionID.PREVIOUS_THEME -> NoneAction
        ActionID.TOGGLE_FULL_SCREEN -> NoneAction
        ActionID.TOGGLE_ORIENTATION -> NoneAction
        ActionID.TOGGLE_BOOK_CSS_ENABLED -> NoneAction

        ActionID.CHANGE_PAGE_MARGINS -> NoneAction
        ActionID.CHANGE_TEXT_SIZE -> ChangeSettingAction(SettingActionID.TEXT_SIZE, settings.font::sizeDip, SettingValues.TEXT_SIZE)
        ActionID.CHANGE_TEXT_LINE_HEIGHT -> ChangeSettingAction(SettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER)
        ActionID.CHANGE_TEXT_GAMMA -> ChangeSettingAction(SettingActionID.TEXT_GAMMA, settings.theme::textGammaCorrection, SettingValues.GAMMA_CORRECTION)
        ActionID.CHANGE_TEXT_STROKE_WIDTH -> ChangeSettingAction(SettingActionID.TEXT_STROKE_WIDTH, settings.font::strokeWidthEm, SettingValues.TEXT_STROKE_WIDTH)
        ActionID.CHANGE_TEXT_SCALE_X -> ChangeSettingAction(SettingActionID.TEXT_SCALE_X, settings.font::scaleX, SettingValues.TEXT_SCALE_X)
        ActionID.CHANGE_TEXT_LETTER_SPACING -> ChangeSettingAction(SettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING)
        ActionID.CHANGE_SCREEN_BRIGHTNESS -> NoneAction

        ActionID.INCREASE_PAGE_MARGINS -> NoneAction
        ActionID.INCREASE_TEXT_SIZE -> IncreaseSettingAction(SettingActionID.TEXT_SIZE, settings.font::sizeDip, SettingValues.TEXT_SIZE)
        ActionID.INCREASE_TEXT_LINE_HEIGHT -> IncreaseSettingAction(SettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER)
        ActionID.INCREASE_TEXT_GAMMA -> IncreaseSettingAction(SettingActionID.TEXT_GAMMA, settings.theme::textGammaCorrection, SettingValues.GAMMA_CORRECTION)
        ActionID.INCREASE_TEXT_STROKE_WIDTH -> IncreaseSettingAction(SettingActionID.TEXT_STROKE_WIDTH, settings.font::strokeWidthEm, SettingValues.TEXT_STROKE_WIDTH)
        ActionID.INCREASE_TEXT_SCALE_X -> IncreaseSettingAction(SettingActionID.TEXT_SCALE_X, settings.font::scaleX, SettingValues.TEXT_SCALE_X)
        ActionID.INCREASE_TEXT_LETTER_SPACING -> IncreaseSettingAction(SettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING)
        ActionID.INCREASE_SCREEN_BRIGHTNESS -> NoneAction

        ActionID.DECREASE_PAGE_MARGINS -> NoneAction
        ActionID.DECREASE_TEXT_SIZE -> DecreaseSettingsAction(SettingActionID.TEXT_SIZE, settings.font::sizeDip, SettingValues.TEXT_SIZE)
        ActionID.DECREASE_TEXT_LINE_HEIGHT -> DecreaseSettingsAction(SettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, SettingValues.LINE_HEIGHT_MULTIPLIER)
        ActionID.DECREASE_TEXT_GAMMA -> DecreaseSettingsAction(SettingActionID.TEXT_GAMMA, settings.theme::textGammaCorrection, SettingValues.GAMMA_CORRECTION)
        ActionID.DECREASE_TEXT_STROKE_WIDTH -> DecreaseSettingsAction(SettingActionID.TEXT_STROKE_WIDTH, settings.font::strokeWidthEm, SettingValues.TEXT_STROKE_WIDTH)
        ActionID.DECREASE_TEXT_SCALE_X -> DecreaseSettingsAction(SettingActionID.TEXT_SCALE_X, settings.font::scaleX, SettingValues.TEXT_SCALE_X)
        ActionID.DECREASE_TEXT_LETTER_SPACING -> DecreaseSettingsAction(SettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, SettingValues.TEXT_LETTER_SPACING)
        ActionID.DECREASE_SCREEN_BRIGHTNESS -> NoneAction
    }

    private fun repeatAction(action: () -> Unit) = object : RepeatAction(Dispatchers.Main, periodMillis = 400) {
        override fun perform() = action()
    }

    private inner class ChangeSettingAction(
            private val id: SettingActionID,
            private val property: KMutableProperty0<Float>,
            private val values: FloatArray
    ) : Action {
        private val SENSITIVITY = 16F * density

        private var deltaFromLast = 0F

        override fun startChange() {
            showPopup()
            deltaFromLast = 0F
        }

        override fun change(delta: Float) {
            deltaFromLast += delta
            if (abs(deltaFromLast) >= SENSITIVITY) {
                val indexDelta: Int = round(deltaFromLast / SENSITIVITY)
                deltaFromLast -= indexDelta * SENSITIVITY
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

    private abstract inner class DeltaSettingAction(
            private val id: SettingActionID,
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

    private inner class IncreaseSettingAction(
            id: SettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSettingAction(id, property, values, 1)

    private inner class DecreaseSettingsAction(
            id: SettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSettingAction(id, property, values, -1)

    private fun setBookSetting(values: FloatArray, property: KMutableProperty0<Float>, offset: Int) {
        val oldValue = property.get()
        val newValue = chooseSettingValue(values, oldValue, offset)
        if (newValue != oldValue) {
            property.set(newValue)
        }
    }
}