package com.dmi.perfectreader.reader.action

import android.content.Context
import com.dmi.perfectreader.R

fun actionName(context: Context, id: ReaderActionID): String = when (id) {
    ReaderActionID.NONE -> TODO()
    ReaderActionID.TOGGLE_MENU -> TODO()
    ReaderActionID.EXIT_ACTIVITY -> TODO()
    ReaderActionID.GO_TO_LIBRARY_LAST -> TODO()
    ReaderActionID.GO_TO_LIBRARY_FAVOURITE -> TODO()
    ReaderActionID.GO_TO_LIBRARY_FILES -> TODO()
    ReaderActionID.GO_TO_LIBRARY_OPDS -> TODO()
    ReaderActionID.GO_TO_SETTINGS -> TODO()
    ReaderActionID.GO_TO_TABLE_OF_CONTENTS -> TODO()
    ReaderActionID.GO_TO_BOOKMARKS -> TODO()
    ReaderActionID.GO_TO_NOTES -> TODO()
    ReaderActionID.GO_TO_MARKS -> TODO()
    ReaderActionID.GO_TO_NEXT_BOOK_IN_HISTORY -> TODO()
    ReaderActionID.GO_TO_PREVIOUS_BOOK_IN_HISTORY -> TODO()
    ReaderActionID.SHOW_FAST_SETTINGS -> TODO()
    ReaderActionID.SHOW_SEARCH -> TODO()
    ReaderActionID.SHOW_GO_PAGE -> TODO()
    ReaderActionID.SHOW_BOOK_INFO -> TODO()
    ReaderActionID.TOGGLE_AUTOSCROLL -> TODO()
    ReaderActionID.TOGGLE_TEXT_SPEECH -> TODO()
    ReaderActionID.ADD_BOOKMARK -> TODO()
    ReaderActionID.ADD_BOOK_TO_FAVOURITE -> TODO()

    ReaderActionID.SCROLL -> TODO()
    ReaderActionID.GO_NEXT_PAGE -> TODO()
    ReaderActionID.GO_PREVIOUS_PAGE -> TODO()
    ReaderActionID.GO_NEXT_PAGE_WITHOUT_ANIMATION -> TODO()
    ReaderActionID.GO_PREVIOUS_PAGE_WITHOUT_ANIMATION -> TODO()
    ReaderActionID.GO_NEXT_PAGE_10 -> TODO()
    ReaderActionID.GO_PREVIOUS_PAGE_10 -> TODO()
    ReaderActionID.GO_BOOK_BEGIN -> TODO()
    ReaderActionID.GO_BOOK_END -> TODO()
    ReaderActionID.GO_NEXT_CHAPTER -> TODO()
    ReaderActionID.GO_PREVIOUS_CHAPTER -> TODO()
    ReaderActionID.GO_BACK_BY_HISTORY -> TODO()
    ReaderActionID.GO_FORWARD_BY_HISTORY -> TODO()

    ReaderActionID.SELECT_WORD -> TODO()
    ReaderActionID.SELECT_WORD_AT_CENTER -> TODO()
    ReaderActionID.TRANSLATE_WORD -> TODO()
    ReaderActionID.SEARCH_WORD -> TODO()
    ReaderActionID.WIKI_WORD -> TODO()

    ReaderActionID.NEXT_THEME -> TODO()
    ReaderActionID.PREVIOUS_THEME -> TODO()
    ReaderActionID.TOGGLE_FULL_SCREEN -> TODO()
    ReaderActionID.TOGGLE_ORIENTATION -> TODO()
    ReaderActionID.TOGGLE_BOOK_CSS_ENABLED -> TODO()

    ReaderActionID.CHANGE_PAGE_MARGINS -> settingActionName(context, ReaderSettingActionID.PAGE_MARGINS)
    ReaderActionID.CHANGE_TEXT_SIZE -> settingActionName(context, ReaderSettingActionID.TEXT_SIZE)
    ReaderActionID.CHANGE_TEXT_LINE_HEIGHT -> settingActionName(context, ReaderSettingActionID.TEXT_LINE_HEIGHT)
    ReaderActionID.CHANGE_TEXT_GAMMA -> settingActionName(context, ReaderSettingActionID.TEXT_GAMMA)
    ReaderActionID.CHANGE_TEXT_COLOR_GAMMA -> settingActionName(context, ReaderSettingActionID.TEXT_COLOR_GAMMA)
    ReaderActionID.CHANGE_TEXT_COLOR_CONTRAST -> settingActionName(context, ReaderSettingActionID.TEXT_COLOR_CONTRAST)
    ReaderActionID.CHANGE_TEXT_COLOR_BRIGHTNESS -> settingActionName(context, ReaderSettingActionID.TEXT_COLOR_BRIGHTNESS)
    ReaderActionID.CHANGE_TEXT_STROKE_WIDTH -> settingActionName(context, ReaderSettingActionID.TEXT_STROKE_WIDTH)
    ReaderActionID.CHANGE_TEXT_SCALE_X -> settingActionName(context, ReaderSettingActionID.TEXT_SCALE_X)
    ReaderActionID.CHANGE_TEXT_LETTER_SPACING -> settingActionName(context, ReaderSettingActionID.TEXT_LETTER_SPACING)
    ReaderActionID.CHANGE_SCREEN_BRIGHTNESS -> settingActionName(context, ReaderSettingActionID.SCREEN_BRIGHTNESS)

    ReaderActionID.INCREASE_PAGE_MARGINS -> settingIncreaseActionName(context, ReaderSettingActionID.PAGE_MARGINS)
    ReaderActionID.INCREASE_TEXT_SIZE -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_SIZE)
    ReaderActionID.INCREASE_TEXT_LINE_HEIGHT -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_LINE_HEIGHT)
    ReaderActionID.INCREASE_TEXT_GAMMA -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_GAMMA)
    ReaderActionID.INCREASE_TEXT_COLOR_GAMMA -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_GAMMA)
    ReaderActionID.INCREASE_TEXT_COLOR_CONTRAST -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_CONTRAST)
    ReaderActionID.INCREASE_TEXT_COLOR_BRIGHTNESS -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_BRIGHTNESS)
    ReaderActionID.INCREASE_TEXT_STROKE_WIDTH -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_STROKE_WIDTH)
    ReaderActionID.INCREASE_TEXT_SCALE_X -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_SCALE_X)
    ReaderActionID.INCREASE_TEXT_LETTER_SPACING -> settingIncreaseActionName(context, ReaderSettingActionID.TEXT_LETTER_SPACING)
    ReaderActionID.INCREASE_SCREEN_BRIGHTNESS -> settingIncreaseActionName(context, ReaderSettingActionID.SCREEN_BRIGHTNESS)

    ReaderActionID.DECREASE_PAGE_MARGINS -> settingDecreaseActionName(context, ReaderSettingActionID.PAGE_MARGINS)
    ReaderActionID.DECREASE_TEXT_SIZE -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_SIZE)
    ReaderActionID.DECREASE_TEXT_LINE_HEIGHT -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_LINE_HEIGHT)
    ReaderActionID.DECREASE_TEXT_GAMMA -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_GAMMA)
    ReaderActionID.DECREASE_TEXT_COLOR_GAMMA -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_GAMMA)
    ReaderActionID.DECREASE_TEXT_COLOR_CONTRAST -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_CONTRAST)
    ReaderActionID.DECREASE_TEXT_COLOR_BRIGHTNESS -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_COLOR_BRIGHTNESS)
    ReaderActionID.DECREASE_TEXT_STROKE_WIDTH -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_STROKE_WIDTH)
    ReaderActionID.DECREASE_TEXT_SCALE_X -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_SCALE_X)
    ReaderActionID.DECREASE_TEXT_LETTER_SPACING -> settingDecreaseActionName(context, ReaderSettingActionID.TEXT_LETTER_SPACING)
    ReaderActionID.DECREASE_SCREEN_BRIGHTNESS -> settingDecreaseActionName(context, ReaderSettingActionID.SCREEN_BRIGHTNESS)
}

fun settingActionName(context: Context, id: ReaderSettingActionID): String = when (id) {
    ReaderSettingActionID.NONE -> context.getString(R.string.actionNone)
    ReaderSettingActionID.PAGE_MARGINS -> context.getString(R.string.actionChangePageMargins)
    ReaderSettingActionID.TEXT_SIZE -> context.getString(R.string.actionChangeTextSize)
    ReaderSettingActionID.TEXT_LINE_HEIGHT -> context.getString(R.string.actionChangeTextLineHeight)
    ReaderSettingActionID.TEXT_GAMMA -> context.getString(R.string.actionChangeTextGamma)
    ReaderSettingActionID.TEXT_COLOR_GAMMA -> context.getString(R.string.actionChangeTextColorGamma)
    ReaderSettingActionID.TEXT_COLOR_CONTRAST -> context.getString(R.string.actionChangeTextColorContrast)
    ReaderSettingActionID.TEXT_COLOR_BRIGHTNESS -> context.getString(R.string.actionChangeTextColorBrightness)
    ReaderSettingActionID.TEXT_STROKE_WIDTH -> context.getString(R.string.actionChangeTextStrokeWidth)
    ReaderSettingActionID.TEXT_SCALE_X -> context.getString(R.string.actionChangeTextScaleX)
    ReaderSettingActionID.TEXT_LETTER_SPACING -> context.getString(R.string.actionChangeTextLetterSpacing)
    ReaderSettingActionID.SCREEN_BRIGHTNESS -> context.getString(R.string.actionChangeScreenBrightness)
}

fun settingIncreaseActionName(context: Context, id: ReaderSettingActionID): String {
    val name = settingActionName(context, id)
    val tag = context.getString(R.string.actionSettingIncrease)
    return "$name ($tag)"
}

fun settingDecreaseActionName(context: Context, id: ReaderSettingActionID): String {
    val name = settingActionName(context, id)
    val tag = context.getString(R.string.actionSettingDecrease)
    return "$name ($tag)"
}