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
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginMultiplier: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,

        val textFontFamily: String,
        val textIsBold: Boolean,
        val textIsItalic: Boolean,
        val textSizeMultiplier: Float,
        val letterSpacing: Float,
        val textScaleX: Float,
        val textSkewX: Float,
        val textStrokeWidth: Float,
        val textColor: Color,
        val textAntialiasing: Boolean,
        val textHinting: Boolean,
        val textSubpixelPositioning: Boolean,

        val textShadowEnabled: Boolean,
        val textShadowOffsetX: Float,
        val textShadowOffsetY: Float,
        val textShadowStrokeWidth: Float,
        val textShadowBlurRadius: Float,
        val textShadowColor: Color,

        val selectionColor: Color,

        val imageSourceScale: Float,
        val imageScaleFiltered: Boolean
)

fun settingsLayoutConfig(context: Context, settings: UserSettings) = ContentConfig(
        context.displayMetrics.density,

        defaultLocale = defaultLocale(context, settings),
        ignoreDeclaredLocale = settings[AnalyzeKeys.ignoreDeclaredLanguage],

        firstLineIndent = settings[FormatKeys.firstLineIndent],
        textAlign = settings[FormatKeys.textAlign],
        lineHeightMultiplier = settings[FormatKeys.lineHeightMultiplier],
        paragraphVerticalMarginMultiplier = settings[FormatKeys.paragraphVerticalMarginMultiplier],
        hangingConfig = if (settings[FormatKeys.hangingPunctuation]) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings[FormatKeys.hyphenation],
        letterSpacing = settings[FormatKeys.letterSpacing],

        textFontFamily = settings[FormatKeys.textFontFamily],
        textIsBold = settings[FormatKeys.textIsBold],
        textIsItalic = settings[FormatKeys.textIsItalic],
        textSizeMultiplier = settings[FormatKeys.textSizeMultiplier],
        textScaleX = settings[FormatKeys.textScaleX],
        textSkewX = settings[FormatKeys.textSkewX],
        textStrokeWidth = settings[FormatKeys.textStrokeWidth],
        textColor = Color(settings[FormatKeys.textColor]),
        textAntialiasing = settings[FormatKeys.textAntialiasing],
        textHinting = settings[FormatKeys.textHinting],
        textSubpixelPositioning = settings[FormatKeys.textSubpixelPositioning],

        textShadowEnabled = settings[FormatKeys.textShadowEnabled],
        textShadowOffsetX = settings[FormatKeys.textShadowOffsetX],
        textShadowOffsetY = settings[FormatKeys.textShadowOffsetY],
        textShadowStrokeWidth = settings[FormatKeys.textShadowStrokeWidth],
        textShadowBlurRadius = settings[FormatKeys.textShadowBlurRadius],
        textShadowColor = Color(settings[FormatKeys.textShadowColor]),

        selectionColor = Color(settings[UIKeys.selectionColor]),

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