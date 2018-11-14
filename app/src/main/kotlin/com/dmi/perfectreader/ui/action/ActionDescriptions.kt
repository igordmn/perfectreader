package com.dmi.perfectreader.ui.action

import android.content.Context
import com.dmi.perfectreader.R
import com.dmi.util.android.view.string

fun actionNames(context: Context, id: ActionID): List<String> {
    fun show() = context.string(R.string.actionShowLibrary)

    return when (id) {
        ActionID.NONE -> TODO()

        ActionID.SHOW_MENU -> TODO()
        ActionID.SHOW_LIBRARY -> TODO()
        ActionID.SHOW_SETTINGS -> TODO()
        ActionID.SHOW_SEARCH -> TODO()
        ActionID.SHOW_TABLE_OF_CONTENTS -> TODO()

        ActionID.SCROLL_PAGE -> TODO()
        ActionID.GO_PAGE_NEXT -> TODO()
        ActionID.GO_PAGE_PREVIOUS -> TODO()
        ActionID.GO_PAGE_NEXT_5 -> TODO()
        ActionID.GO_PAGE_PREVIOUS_5 -> TODO()
        ActionID.GO_PAGE_NEXT_10 -> TODO()
        ActionID.GO_PAGE_PREVIOUS_10 -> TODO()
        ActionID.GO_PAGE_NEXT_20 -> TODO()
        ActionID.GO_PAGE_PREVIOUS_20 -> TODO()
        ActionID.GO_PAGE_NEXT_ANIMATED -> TODO()
        ActionID.GO_PAGE_PREVIOUS_ANIMATED -> TODO()
        ActionID.GO_PAGE_NEXT_5_ANIMATED -> TODO()
        ActionID.GO_PAGE_PREVIOUS_5_ANIMATED -> TODO()
        ActionID.GO_PAGE_NEXT_10_ANIMATED -> TODO()
        ActionID.GO_PAGE_PREVIOUS_10_ANIMATED -> TODO()
        ActionID.GO_PAGE_NEXT_20_ANIMATED -> TODO()
        ActionID.GO_PAGE_PREVIOUS_20_ANIMATED -> TODO()

        ActionID.GO_CHAPTER_NEXT -> TODO()
        ActionID.GO_CHAPTER_PREVIOUS -> TODO()
        ActionID.GO_BOOK_BEGIN -> TODO()
        ActionID.GO_BOOK_END -> TODO()

        ActionID.WORD_SELECT -> TODO()

        ActionID.SETTINGS_FONT_SIZE_CHANGE -> TODO()
        ActionID.SETTINGS_FONT_WIDTH_CHANGE -> TODO()
        ActionID.SETTINGS_FONT_BOLDNESS_CHANGE -> TODO()
        ActionID.SETTINGS_FONT_SKEW_CHANGE -> TODO()
        ActionID.SETTINGS_FORMAT_PADDING_CHANGE -> TODO()
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_CHANGE -> TODO()
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_CHANGE -> TODO()
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_CHANGE -> TODO()
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_CHANGE -> TODO()
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_CHANGE -> TODO()

        ActionID.SETTINGS_FONT_SIZE_INCREASE -> TODO()
        ActionID.SETTINGS_FONT_WIDTH_INCREASE -> TODO()
        ActionID.SETTINGS_FONT_BOLDNESS_INCREASE -> TODO()
        ActionID.SETTINGS_FONT_SKEW_INCREASE -> TODO()
        ActionID.SETTINGS_FORMAT_PADDING_INCREASE -> TODO()
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_INCREASE -> TODO()
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_INCREASE -> TODO()
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_INCREASE -> TODO()
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_INCREASE -> TODO()
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_INCREASE -> TODO()

        ActionID.SETTINGS_FONT_SIZE_DECREASE -> TODO()
        ActionID.SETTINGS_FONT_WIDTH_DECREASE -> TODO()
        ActionID.SETTINGS_FONT_BOLDNESS_DECREASE -> TODO()
        ActionID.SETTINGS_FONT_SKEW_DECREASE -> TODO()
        ActionID.SETTINGS_FORMAT_PADDING_DECREASE -> TODO()
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_DECREASE -> TODO()
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_DECREASE -> TODO()
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_DECREASE -> TODO()
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_DECREASE -> TODO()
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_DECREASE -> TODO()

        ActionID.SETTINGS_THEME_STYLE_SWITCH -> TODO()
        ActionID.SETTINGS_SCREEN_FOOTER_SWITCH -> TODO()
    }
}

fun numberSettingActionName(context: Context, id: NumberSettingActionID): String = when (id) {
    NumberSettingActionID.FONT_SIZE -> context.string(R.string.settingsUIFontSize)
    NumberSettingActionID.FONT_WIDTH -> context.string(R.string.settingsUIFontWidth)
    NumberSettingActionID.FONT_BOLDNESS -> context.string(R.string.settingsUIFontBoldness)
    NumberSettingActionID.FONT_SKEW -> context.string(R.string.settingsUIFontSkew)
    NumberSettingActionID.FORMAT_PADDING -> context.string(R.string.settingsUIFormatPadding)
    NumberSettingActionID.FORMAT_LINE_HEIGHT -> context.string(R.string.settingsUIFormatLineHeight)
    NumberSettingActionID.FORMAT_LETTER_SPACING -> context.string(R.string.settingsUIFormatLetterSpacing)
    NumberSettingActionID.FORMAT_PARAGRAPH_SPACING -> context.string(R.string.settingsUIFormatParagraphSpacing)
    NumberSettingActionID.FORMAT_FIRST_LINE_INDENT -> context.string(R.string.settingsUIFormatFirstLineIndent)
    NumberSettingActionID.SCREEN_BRIGHTNESS -> context.string(R.string.settingsUIScreenBrightness)
}