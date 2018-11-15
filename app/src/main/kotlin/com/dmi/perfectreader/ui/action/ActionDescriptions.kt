package com.dmi.perfectreader.ui.action

import android.content.Context
import androidx.annotation.StringRes
import com.dmi.perfectreader.R
import com.dmi.util.android.view.string

class ActionDescription(val first: String, val second: String? = null, val third: String? = null) {
    override fun toString() = when {
        second != null && third != null -> "$second\n(${third.toLowerCase()})"
        second != null -> "$second"
        else -> first
    }
}

fun actionDescription(context: Context, id: ActionID): ActionDescription {
    fun ActionDescription(
            @StringRes firstRes: Int,
            @StringRes secondRes: Int? = null,
            @StringRes thirdRes: Int? = null
    ) = ActionDescription(
            context.string(firstRes),
            secondRes?.let(context::string),
            thirdRes?.let(context::string)
    )

    return when (id) {
        ActionID.NONE -> ActionDescription(R.string.actionNone)

        ActionID.SHOW_MENU -> ActionDescription(R.string.actionShow, R.string.actionShowMenu)
        ActionID.SHOW_LIBRARY -> ActionDescription(R.string.actionShow, R.string.actionShowLibrary)
        ActionID.SHOW_SETTINGS -> ActionDescription(R.string.actionShow, R.string.actionShowSettings)
        ActionID.SHOW_SEARCH -> ActionDescription(R.string.actionShow, R.string.actionShowSearch)
        ActionID.SHOW_TABLE_OF_CONTENTS -> ActionDescription(R.string.actionShow, R.string.actionShowTableOfContents)

        ActionID.SCROLL_PAGE -> ActionDescription(R.string.actionScrollPage)
        ActionID.GO_PAGE_NEXT -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_PREVIOUS -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_NEXT_5 -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext5, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_PREVIOUS_5 -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious5, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_NEXT_10 -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext10, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_PREVIOUS_10 -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious10, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_NEXT_20 -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext20, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_PREVIOUS_20 -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious20, R.string.actionGoWithoutAnimation)
        ActionID.GO_PAGE_NEXT_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_PREVIOUS_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_NEXT_5_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext5, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_PREVIOUS_5_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious5, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_NEXT_10_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext10, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_PREVIOUS_10_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious10, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_NEXT_20_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPageNext20, R.string.actionGoWithAnimation)
        ActionID.GO_PAGE_PREVIOUS_20_ANIMATED -> ActionDescription(R.string.actionGo, R.string.actionGoPagePrevious20, R.string.actionGoWithAnimation)

        ActionID.GO_CHAPTER_NEXT -> ActionDescription(R.string.actionGo, R.string.actionGoChapterNext)
        ActionID.GO_CHAPTER_PREVIOUS -> ActionDescription(R.string.actionGo, R.string.actionGoChapterPrevious)
        ActionID.GO_BOOK_BEGIN -> ActionDescription(R.string.actionGo, R.string.actionGoBookBegin)
        ActionID.GO_BOOK_END -> ActionDescription(R.string.actionGo, R.string.actionGoBookEnd)

        ActionID.WORD_SELECT -> ActionDescription(R.string.actionWordSelect)

        ActionID.SETTINGS_FONT_SIZE_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSize, R.string.actionSettingChange)
        ActionID.SETTINGS_FONT_WIDTH_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontWidth, R.string.actionSettingChange)
        ActionID.SETTINGS_FONT_BOLDNESS_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontBoldness, R.string.actionSettingChange)
        ActionID.SETTINGS_FONT_SKEW_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSkew, R.string.actionSettingChange)
        ActionID.SETTINGS_FORMAT_PADDING_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatPadding, R.string.actionSettingChange)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLineHeight, R.string.actionSettingChange)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLetterSpacing, R.string.actionSettingChange)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatParagraphSpacing, R.string.actionSettingChange)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFirstLineIndent, R.string.actionSettingChange)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_CHANGE -> ActionDescription(R.string.actionSetting, R.string.actionSettingBrightness, R.string.actionSettingChange)

        ActionID.SETTINGS_FONT_SIZE_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSize, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FONT_WIDTH_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontWidth, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FONT_BOLDNESS_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontBoldness, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FONT_SKEW_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSkew, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FORMAT_PADDING_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatPadding, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLineHeight, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLetterSpacing, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatParagraphSpacing, R.string.actionSettingIncrease)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFirstLineIndent, R.string.actionSettingIncrease)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_INCREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingBrightness, R.string.actionSettingIncrease)

        ActionID.SETTINGS_FONT_SIZE_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSize, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FONT_WIDTH_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontWidth, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FONT_BOLDNESS_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontBoldness, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FONT_SKEW_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFontSkew, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FORMAT_PADDING_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatPadding, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FORMAT_LINE_HEIGHT_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLineHeight, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FORMAT_LETTER_SPACING_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatLetterSpacing, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FORMAT_PARAGRAPH_SPACING_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFormatParagraphSpacing, R.string.actionSettingDecrease)
        ActionID.SETTINGS_FORMAT_FIRST_LINE_INDENT_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingFirstLineIndent, R.string.actionSettingDecrease)
        ActionID.SETTINGS_SCREEN_BRIGHTNESS_DECREASE -> ActionDescription(R.string.actionSetting, R.string.actionSettingBrightness, R.string.actionSettingDecrease)

        ActionID.SETTINGS_THEME_STYLE_SWITCH -> ActionDescription(R.string.actionSetting, R.string.actionSettingStyle)
        ActionID.SETTINGS_SCREEN_FOOTER_SWITCH -> ActionDescription(R.string.actionSetting, R.string.actionSettingFooter)
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