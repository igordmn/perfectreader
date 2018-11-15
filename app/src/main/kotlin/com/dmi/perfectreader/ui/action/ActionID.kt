package com.dmi.perfectreader.ui.action

enum class ActionID {
    NONE,

    SCROLL_PAGE,
    WORD_SELECT,

    SHOW_MENU,
    SHOW_LIBRARY,
    SHOW_SETTINGS,
    SHOW_SEARCH,
    SHOW_TABLE_OF_CONTENTS,

    GO_PAGE_NEXT,
    GO_PAGE_PREVIOUS,
    GO_PAGE_NEXT_5,
    GO_PAGE_PREVIOUS_5,
    GO_PAGE_NEXT_10,
    GO_PAGE_PREVIOUS_10,
    GO_PAGE_NEXT_20,
    GO_PAGE_PREVIOUS_20,
    GO_PAGE_NEXT_ANIMATED,
    GO_PAGE_PREVIOUS_ANIMATED,
    GO_PAGE_NEXT_5_ANIMATED,
    GO_PAGE_PREVIOUS_5_ANIMATED,
    GO_PAGE_NEXT_10_ANIMATED,
    GO_PAGE_PREVIOUS_10_ANIMATED,
    GO_PAGE_NEXT_20_ANIMATED,
    GO_PAGE_PREVIOUS_20_ANIMATED,

    GO_CHAPTER_NEXT,
    GO_CHAPTER_PREVIOUS,
    GO_BOOK_BEGIN,
    GO_BOOK_END,

    SETTINGS_FONT_SIZE_CHANGE,
    SETTINGS_FONT_WIDTH_CHANGE,
    SETTINGS_FONT_BOLDNESS_CHANGE,
    SETTINGS_FONT_SKEW_CHANGE,
    SETTINGS_FORMAT_PADDING_CHANGE,
    SETTINGS_FORMAT_LINE_HEIGHT_CHANGE,
    SETTINGS_FORMAT_LETTER_SPACING_CHANGE,
    SETTINGS_FORMAT_PARAGRAPH_SPACING_CHANGE,
    SETTINGS_FORMAT_FIRST_LINE_INDENT_CHANGE,
    SETTINGS_SCREEN_BRIGHTNESS_CHANGE,

    SETTINGS_FONT_SIZE_INCREASE,
    SETTINGS_FONT_WIDTH_INCREASE,
    SETTINGS_FONT_BOLDNESS_INCREASE,
    SETTINGS_FONT_SKEW_INCREASE,
    SETTINGS_FORMAT_PADDING_INCREASE,
    SETTINGS_FORMAT_LINE_HEIGHT_INCREASE,
    SETTINGS_FORMAT_LETTER_SPACING_INCREASE,
    SETTINGS_FORMAT_PARAGRAPH_SPACING_INCREASE,
    SETTINGS_FORMAT_FIRST_LINE_INDENT_INCREASE,
    SETTINGS_SCREEN_BRIGHTNESS_INCREASE,

    SETTINGS_FONT_SIZE_DECREASE,
    SETTINGS_FONT_WIDTH_DECREASE,
    SETTINGS_FONT_BOLDNESS_DECREASE,
    SETTINGS_FONT_SKEW_DECREASE,
    SETTINGS_FORMAT_PADDING_DECREASE,
    SETTINGS_FORMAT_LINE_HEIGHT_DECREASE,
    SETTINGS_FORMAT_LETTER_SPACING_DECREASE,
    SETTINGS_FORMAT_PARAGRAPH_SPACING_DECREASE,
    SETTINGS_FORMAT_FIRST_LINE_INDENT_DECREASE,
    SETTINGS_SCREEN_BRIGHTNESS_DECREASE,

    SETTINGS_THEME_STYLE_SWITCH,
    SETTINGS_SCREEN_FOOTER_SWITCH
}

enum class NumberSettingActionID {
    FONT_SIZE,
    FONT_WIDTH,
    FONT_BOLDNESS,
    FONT_SKEW,
    FORMAT_PADDING,
    FORMAT_LINE_HEIGHT,
    FORMAT_LETTER_SPACING,
    FORMAT_PARAGRAPH_SPACING,
    FORMAT_FIRST_LINE_INDENT,
    SCREEN_BRIGHTNESS;
}