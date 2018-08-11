package com.dmi.perfectreader.reader.action

import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.page.PageScroller
import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.settings.Settings
import com.dmi.perfectreader.settings.values.UserSettingValues
import com.dmi.perfectreader.settings.values.chooseSettingValue
import com.dmi.util.action.*
import com.dmi.util.graphic.PositionF
import com.dmi.util.input.TouchArea
import com.dmi.util.system.ApplicationWindow
import kotlinx.coroutines.experimental.android.UI
import java.lang.Math.abs
import java.lang.Math.round
import kotlin.reflect.KMutableProperty0

class ReaderActions(
        private val main: Main,
        private val window: ApplicationWindow,
        private val reader: Reader,
        private val density: Float = main.density,
        private val settings: Settings = main.settings
) {
    private val book: Book get() = reader.book

    operator fun get(id: ReaderActionID): Action = when (id) {
        ReaderActionID.NONE -> NoneAction
        ReaderActionID.TOGGLE_MENU -> performAction { reader.toggleMenu() }
        ReaderActionID.CLOSE_APPLICATION_WINDOW -> performAction { window.close() }
        ReaderActionID.GO_TO_LIBRARY_LAST -> NoneAction
        ReaderActionID.GO_TO_LIBRARY_FAVOURITE -> NoneAction
        ReaderActionID.GO_TO_LIBRARY_FILES -> NoneAction
        ReaderActionID.GO_TO_LIBRARY_OPDS -> NoneAction
        ReaderActionID.GO_TO_SETTINGS -> NoneAction
        ReaderActionID.GO_TO_TABLE_OF_CONTENTS -> NoneAction
        ReaderActionID.GO_TO_BOOKMARKS -> NoneAction
        ReaderActionID.GO_TO_NOTES -> NoneAction
        ReaderActionID.GO_TO_MARKS -> NoneAction
        ReaderActionID.GO_TO_NEXT_BOOK_IN_HISTORY -> NoneAction
        ReaderActionID.GO_TO_PREVIOUS_BOOK_IN_HISTORY -> NoneAction
        ReaderActionID.SHOW_FAST_SETTINGS -> NoneAction
        ReaderActionID.SHOW_SEARCH -> NoneAction
        ReaderActionID.SHOW_GO_PAGE -> NoneAction
        ReaderActionID.SHOW_BOOK_INFO -> NoneAction
        ReaderActionID.TOGGLE_AUTOSCROLL -> NoneAction
        ReaderActionID.TOGGLE_TEXT_SPEECH -> NoneAction
        ReaderActionID.ADD_BOOKMARK -> NoneAction
        ReaderActionID.ADD_BOOK_TO_FAVOURITE -> NoneAction

        ReaderActionID.SCROLL -> object : Action {
            lateinit var scroller: PageScroller

            override fun startScroll(area: TouchArea) = run { scroller = book.scroll() }
            override fun scroll(delta: PositionF) = scroller.scroll(-delta)
            override fun endScroll(velocity: PositionF) = scroller.end(-velocity)
            override fun cancelScroll() = scroller.cancel()
        }
        ReaderActionID.GO_NEXT_PAGE -> repeatAction { book.animateRelative(1) }
        ReaderActionID.GO_PREVIOUS_PAGE -> repeatAction { book.animateRelative(-1) }
        ReaderActionID.GO_NEXT_PAGE_WITHOUT_ANIMATION -> repeatAction { book.goRelative(1) }
        ReaderActionID.GO_PREVIOUS_PAGE_WITHOUT_ANIMATION -> repeatAction { book.goRelative(-1) }
        ReaderActionID.GO_NEXT_PAGE_10 -> performAction { book.animateRelative(10) }
        ReaderActionID.GO_PREVIOUS_PAGE_10 -> performAction { book.animateRelative(-10) }
        ReaderActionID.GO_BOOK_BEGIN -> NoneAction
        ReaderActionID.GO_BOOK_END -> NoneAction
        ReaderActionID.GO_NEXT_CHAPTER -> NoneAction
        ReaderActionID.GO_PREVIOUS_CHAPTER -> NoneAction
        ReaderActionID.GO_BACK_BY_HISTORY -> NoneAction
        ReaderActionID.GO_FORWARD_BY_HISTORY -> NoneAction

        ReaderActionID.SELECT_WORD -> touchAction { reader.selection = reader.selection(book.selections?.at(it.position)) }
        ReaderActionID.SELECT_WORD_AT_CENTER -> performAction { reader.selection = reader.selection(book.selections?.center()) }
        ReaderActionID.TRANSLATE_WORD -> NoneAction
        ReaderActionID.SEARCH_WORD -> NoneAction
        ReaderActionID.WIKI_WORD -> NoneAction

        ReaderActionID.NEXT_THEME -> NoneAction
        ReaderActionID.PREVIOUS_THEME -> NoneAction
        ReaderActionID.TOGGLE_FULL_SCREEN -> NoneAction
        ReaderActionID.TOGGLE_ORIENTATION -> NoneAction
        ReaderActionID.TOGGLE_BOOK_CSS_ENABLED -> NoneAction

        ReaderActionID.CHANGE_PAGE_MARGINS -> NoneAction
        ReaderActionID.CHANGE_TEXT_SIZE -> ChangeSettingAction(ReaderSettingActionID.TEXT_SIZE, settings.format::textSizeDip, UserSettingValues.TEXT_SIZE)
        ReaderActionID.CHANGE_TEXT_LINE_HEIGHT -> ChangeSettingAction(ReaderSettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, UserSettingValues.LINE_HEIGHT_MULTIPLIER)
        ReaderActionID.CHANGE_TEXT_GAMMA -> ChangeSettingAction(ReaderSettingActionID.TEXT_GAMMA, settings.format::pageGammaCorrection, UserSettingValues.GAMMA)
        ReaderActionID.CHANGE_TEXT_COLOR_GAMMA -> NoneAction
        ReaderActionID.CHANGE_TEXT_COLOR_CONTRAST -> NoneAction
        ReaderActionID.CHANGE_TEXT_COLOR_BRIGHTNESS -> NoneAction
        ReaderActionID.CHANGE_TEXT_STROKE_WIDTH -> ChangeSettingAction(ReaderSettingActionID.TEXT_STROKE_WIDTH, settings.format::textStrokeWidthDip, UserSettingValues.TEXT_STROKE_WIDTH)
        ReaderActionID.CHANGE_TEXT_SCALE_X -> ChangeSettingAction(ReaderSettingActionID.TEXT_SCALE_X, settings.format::textScaleX, UserSettingValues.TEXT_SCALE_X)
        ReaderActionID.CHANGE_TEXT_LETTER_SPACING -> ChangeSettingAction(ReaderSettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, UserSettingValues.TEXT_LETTER_SPACING)
        ReaderActionID.CHANGE_SCREEN_BRIGHTNESS -> NoneAction

        ReaderActionID.INCREASE_PAGE_MARGINS -> NoneAction
        ReaderActionID.INCREASE_TEXT_SIZE -> IncreaseSettingAction(ReaderSettingActionID.TEXT_SIZE, settings.format::textSizeDip, UserSettingValues.TEXT_SIZE)
        ReaderActionID.INCREASE_TEXT_LINE_HEIGHT -> IncreaseSettingAction(ReaderSettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, UserSettingValues.LINE_HEIGHT_MULTIPLIER)
        ReaderActionID.INCREASE_TEXT_GAMMA -> IncreaseSettingAction(ReaderSettingActionID.TEXT_GAMMA, settings.format::pageGammaCorrection, UserSettingValues.GAMMA)
        ReaderActionID.INCREASE_TEXT_COLOR_GAMMA -> NoneAction
        ReaderActionID.INCREASE_TEXT_COLOR_CONTRAST -> NoneAction
        ReaderActionID.INCREASE_TEXT_COLOR_BRIGHTNESS -> NoneAction
        ReaderActionID.INCREASE_TEXT_STROKE_WIDTH -> IncreaseSettingAction(ReaderSettingActionID.TEXT_STROKE_WIDTH, settings.format::textStrokeWidthDip, UserSettingValues.TEXT_STROKE_WIDTH)
        ReaderActionID.INCREASE_TEXT_SCALE_X -> IncreaseSettingAction(ReaderSettingActionID.TEXT_SCALE_X, settings.format::textScaleX, UserSettingValues.TEXT_SCALE_X)
        ReaderActionID.INCREASE_TEXT_LETTER_SPACING -> IncreaseSettingAction(ReaderSettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, UserSettingValues.TEXT_LETTER_SPACING)
        ReaderActionID.INCREASE_SCREEN_BRIGHTNESS -> NoneAction

        ReaderActionID.DECREASE_PAGE_MARGINS -> NoneAction
        ReaderActionID.DECREASE_TEXT_SIZE -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_SIZE, settings.format::textSizeDip, UserSettingValues.TEXT_SIZE)
        ReaderActionID.DECREASE_TEXT_LINE_HEIGHT -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_LINE_HEIGHT, settings.format::lineHeightMultiplier, UserSettingValues.LINE_HEIGHT_MULTIPLIER)
        ReaderActionID.DECREASE_TEXT_GAMMA -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_GAMMA, settings.format::pageGammaCorrection, UserSettingValues.GAMMA)
        ReaderActionID.DECREASE_TEXT_COLOR_GAMMA -> NoneAction
        ReaderActionID.DECREASE_TEXT_COLOR_CONTRAST -> NoneAction
        ReaderActionID.DECREASE_TEXT_COLOR_BRIGHTNESS -> NoneAction
        ReaderActionID.DECREASE_TEXT_STROKE_WIDTH -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_STROKE_WIDTH, settings.format::textStrokeWidthDip, UserSettingValues.TEXT_STROKE_WIDTH)
        ReaderActionID.DECREASE_TEXT_SCALE_X -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_SCALE_X, settings.format::textScaleX, UserSettingValues.TEXT_SCALE_X)
        ReaderActionID.DECREASE_TEXT_LETTER_SPACING -> DecreaseSettingsAction(ReaderSettingActionID.TEXT_LETTER_SPACING, settings.format::letterSpacingEm, UserSettingValues.TEXT_LETTER_SPACING)
        ReaderActionID.DECREASE_SCREEN_BRIGHTNESS -> NoneAction
    }

    private fun repeatAction(action: () -> Unit) = object : RepeatAction(UI, periodMillis = 400) {
        override fun perform() = action()
    }

    private inner class ChangeSettingAction(
            private val id: ReaderSettingActionID,
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
            reader.hideActionPopup()
        }

        private fun showPopup() = reader.showActionPopup(id, property.get())
    }

    private abstract inner class DeltaSettingAction(
            private val id: ReaderSettingActionID,
            private val property: KMutableProperty0<Float>,
            private val values: FloatArray,
            private val offset: Int
    ) : RepeatAction(UI, periodMillis = 200) {
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
            reader.hideActionPopup()
            popupShowed = false
            super.endTap()
        }

        private fun showPopup() = reader.showActionPopup(id, property.get())
    }

    private inner class IncreaseSettingAction(
            id: ReaderSettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSettingAction(id, property, values, 1)

    private inner class DecreaseSettingsAction(
            id: ReaderSettingActionID, property: KMutableProperty0<Float>, values: FloatArray
    ) : DeltaSettingAction(id, property, values, -1)

    private fun setBookSetting(values: FloatArray, property: KMutableProperty0<Float>, offset: Int) {
        val oldValue = property.get()
        val newValue = chooseSettingValue(values, oldValue, offset)
        if (newValue != oldValue) {
            property.set(newValue)
        }
    }
}