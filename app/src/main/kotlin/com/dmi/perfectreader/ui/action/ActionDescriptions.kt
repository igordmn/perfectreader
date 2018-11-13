package com.dmi.perfectreader.ui.action

import android.content.Context
import com.dmi.perfectreader.R

fun actionName(context: Context, id: ActionID): String = when (id) {
    ActionID.NONE -> TODO()
    ActionID.SHOW_MENU -> TODO()
    ActionID.GO_TO_LIBRARY_LAST -> TODO()
    ActionID.GO_TO_LIBRARY_FAVOURITE -> TODO()
    ActionID.GO_TO_LIBRARY_FILES -> TODO()
    ActionID.GO_TO_LIBRARY_OPDS -> TODO()
    ActionID.GO_TO_SETTINGS -> TODO()
    ActionID.GO_TO_TABLE_OF_CONTENTS -> TODO()
    ActionID.GO_TO_BOOKMARKS -> TODO()
    ActionID.GO_TO_NOTES -> TODO()
    ActionID.GO_TO_MARKS -> TODO()
    ActionID.GO_TO_NEXT_BOOK_IN_HISTORY -> TODO()
    ActionID.GO_TO_PREVIOUS_BOOK_IN_HISTORY -> TODO()
    ActionID.SHOW_FAST_SETTINGS -> TODO()
    ActionID.SHOW_SEARCH -> TODO()
    ActionID.SHOW_GO_PAGE -> TODO()
    ActionID.SHOW_BOOK_INFO -> TODO()
    ActionID.TOGGLE_AUTOSCROLL -> TODO()
    ActionID.TOGGLE_TEXT_SPEECH -> TODO()
    ActionID.ADD_BOOKMARK -> TODO()
    ActionID.ADD_BOOK_TO_FAVOURITE -> TODO()

    ActionID.SCROLL -> TODO()
    ActionID.GO_NEXT_PAGE -> TODO()
    ActionID.GO_PREVIOUS_PAGE -> TODO()
    ActionID.GO_NEXT_PAGE_WITHOUT_ANIMATION -> TODO()
    ActionID.GO_PREVIOUS_PAGE_WITHOUT_ANIMATION -> TODO()
    ActionID.GO_NEXT_PAGE_10 -> TODO()
    ActionID.GO_PREVIOUS_PAGE_10 -> TODO()
    ActionID.GO_BOOK_BEGIN -> TODO()
    ActionID.GO_BOOK_END -> TODO()
    ActionID.GO_NEXT_CHAPTER -> TODO()
    ActionID.GO_PREVIOUS_CHAPTER -> TODO()
    ActionID.GO_BACK_BY_HISTORY -> TODO()
    ActionID.GO_FORWARD_BY_HISTORY -> TODO()

    ActionID.SELECT_WORD -> TODO()
    ActionID.SELECT_WORD_AT_CENTER -> TODO()
    ActionID.TRANSLATE_WORD -> TODO()
    ActionID.SEARCH_WORD -> TODO()
    ActionID.WIKI_WORD -> TODO()

    ActionID.NEXT_THEME -> TODO()
    ActionID.PREVIOUS_THEME -> TODO()
    ActionID.TOGGLE_FULL_SCREEN -> TODO()
    ActionID.TOGGLE_ORIENTATION -> TODO()
    ActionID.TOGGLE_BOOK_CSS_ENABLED -> TODO()

    ActionID.CHANGE_PAGE_MARGINS -> settingActionName(context, SettingActionID.PAGE_MARGINS)
    ActionID.CHANGE_TEXT_SIZE -> settingActionName(context, SettingActionID.TEXT_SIZE)
    ActionID.CHANGE_TEXT_LINE_HEIGHT -> settingActionName(context, SettingActionID.TEXT_LINE_HEIGHT)
    ActionID.CHANGE_TEXT_GAMMA -> settingActionName(context, SettingActionID.TEXT_GAMMA)
    ActionID.CHANGE_TEXT_STROKE_WIDTH -> settingActionName(context, SettingActionID.TEXT_STROKE_WIDTH)
    ActionID.CHANGE_TEXT_SCALE_X -> settingActionName(context, SettingActionID.TEXT_SCALE_X)
    ActionID.CHANGE_TEXT_LETTER_SPACING -> settingActionName(context, SettingActionID.TEXT_LETTER_SPACING)
    ActionID.CHANGE_SCREEN_BRIGHTNESS -> settingActionName(context, SettingActionID.SCREEN_BRIGHTNESS)

    ActionID.INCREASE_PAGE_MARGINS -> increaseActionName(context, SettingActionID.PAGE_MARGINS)
    ActionID.INCREASE_TEXT_SIZE -> increaseActionName(context, SettingActionID.TEXT_SIZE)
    ActionID.INCREASE_TEXT_LINE_HEIGHT -> increaseActionName(context, SettingActionID.TEXT_LINE_HEIGHT)
    ActionID.INCREASE_TEXT_GAMMA -> increaseActionName(context, SettingActionID.TEXT_GAMMA)
    ActionID.INCREASE_TEXT_STROKE_WIDTH -> increaseActionName(context, SettingActionID.TEXT_STROKE_WIDTH)
    ActionID.INCREASE_TEXT_SCALE_X -> increaseActionName(context, SettingActionID.TEXT_SCALE_X)
    ActionID.INCREASE_TEXT_LETTER_SPACING -> increaseActionName(context, SettingActionID.TEXT_LETTER_SPACING)
    ActionID.INCREASE_SCREEN_BRIGHTNESS -> increaseActionName(context, SettingActionID.SCREEN_BRIGHTNESS)

    ActionID.DECREASE_PAGE_MARGINS -> decreaseActionName(context, SettingActionID.PAGE_MARGINS)
    ActionID.DECREASE_TEXT_SIZE -> decreaseActionName(context, SettingActionID.TEXT_SIZE)
    ActionID.DECREASE_TEXT_LINE_HEIGHT -> decreaseActionName(context, SettingActionID.TEXT_LINE_HEIGHT)
    ActionID.DECREASE_TEXT_GAMMA -> decreaseActionName(context, SettingActionID.TEXT_GAMMA)
    ActionID.DECREASE_TEXT_STROKE_WIDTH -> decreaseActionName(context, SettingActionID.TEXT_STROKE_WIDTH)
    ActionID.DECREASE_TEXT_SCALE_X -> decreaseActionName(context, SettingActionID.TEXT_SCALE_X)
    ActionID.DECREASE_TEXT_LETTER_SPACING -> decreaseActionName(context, SettingActionID.TEXT_LETTER_SPACING)
    ActionID.DECREASE_SCREEN_BRIGHTNESS -> decreaseActionName(context, SettingActionID.SCREEN_BRIGHTNESS)
}

fun settingActionName(context: Context, id: SettingActionID): String = when (id) {
    SettingActionID.NONE -> context.getString(R.string.actionNone)
    SettingActionID.PAGE_MARGINS -> context.getString(R.string.settingsUIFormatPadding)
    SettingActionID.TEXT_SIZE -> context.getString(R.string.settingsUIFontSize)
    SettingActionID.TEXT_LINE_HEIGHT -> context.getString(R.string.settingsUIFormatLineHeight)
    SettingActionID.TEXT_GAMMA -> context.getString(R.string.settingsUIThemeTextGammaCorrection)
    SettingActionID.TEXT_STROKE_WIDTH -> context.getString(R.string.settingsUIFontBoldness)
    SettingActionID.TEXT_SCALE_X -> context.getString(R.string.settingsUIFontWidth)
    SettingActionID.TEXT_LETTER_SPACING -> context.getString(R.string.settingsUIFormatLetterSpacing)
    SettingActionID.SCREEN_BRIGHTNESS -> context.getString(R.string.settingsUIScreenBrightness)
}

fun increaseActionName(context: Context, id: SettingActionID): String {
    val name = settingActionName(context, id)
    val tag = context.getString(R.string.actionSettingIncrease)
    return "$name ($tag)"
}

fun decreaseActionName(context: Context, id: SettingActionID): String {
    val name = settingActionName(context, id)
    val tag = context.getString(R.string.actionSettingDecrease)
    return "$name ($tag)"
}