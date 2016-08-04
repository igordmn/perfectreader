package com.dmi.perfectreader.fragment.book.content.obj.param

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.util.graphic.Color
import org.jetbrains.anko.displayMetrics
import java.util.*
import com.dmi.perfectreader.data.UserSettingKeys.Analyze as AnalyzeKeys
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys
import com.dmi.perfectreader.data.UserSettingKeys.Image as ImageKeys
import com.dmi.perfectreader.data.UserSettingKeys.UI as UIKeys

class ContentConfig(
        val density: Float,
        val defaultLocale: Locale,
        val ignoreDeclaredLocale: Boolean,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val fontSizeMultiplier: Float,
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginMultiplier: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,
        val textRenderConfig: TextRenderConfig,
        val selectionConfig: SelectionConfig,
        val imageSourceScale: Float,
        val imageScaleFiltered: Boolean
)

fun settingsLayoutConfig(context: Context, settings: UserSettings) = ContentConfig(
        context.displayMetrics.density,
        defaultLocale = defaultLocale(context, settings),
        ignoreDeclaredLocale = settings[AnalyzeKeys.ignoreDeclaredLanguage],
        firstLineIndent = settings[FormatKeys.firstLineIndent],
        textAlign = settings[FormatKeys.textAlign],
        fontSizeMultiplier = settings[FormatKeys.fontSizeMultiplier],
        lineHeightMultiplier = settings[FormatKeys.lineHeightMultiplier],
        paragraphVerticalMarginMultiplier = settings[FormatKeys.paragraphVerticalMarginMultiplier],
        hangingConfig = if (settings[FormatKeys.hangingPunctuation]) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings[FormatKeys.hyphenation],
        textRenderConfig = TextRenderConfig(antialias = true, hinting = true, linearScaling = false, subpixel = true),
        selectionConfig = SelectionConfig(
                Color(settings[UIKeys.selectionBackgroundColor])
        ),
        imageSourceScale = if (settings[ImageKeys.sourceScaleByDpi]) context.displayMetrics.density else settings[ImageKeys.sourceScale],
        imageScaleFiltered = settings[ImageKeys.scaleFiltered]
)

private fun defaultLocale(context: Context, settings: UserSettings) =
        if (settings[AnalyzeKeys.defaultLanguageIsSystem]) {
            systemLocale(context)
        } else {
            Locale(settings[AnalyzeKeys.defaultLanguage])
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)