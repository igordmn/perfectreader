package com.dmi.perfectreader.book.content.obj.param

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.util.font.FontCollection
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

        val firstLineIndentEm: Float,
        val textAlign: TextAlign,
        val letterSpacingEm: Float,
        val wordSpacingMultiplier: Float,
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginEm: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,

        val textFontFamily: String,
        val textFontStyle: String,
        val textSizeDip: Float,
        val textScaleX: Float,
        val textSkewX: Float,
        val textStrokeWidthDip: Float,
        val textColor: Color,
        val textAntialiasing: Boolean,
        val textHinting: Boolean,
        val textSubpixelPositioning: Boolean,

        val textShadowEnabled: Boolean,
        val textShadowOffsetXDip: Float,
        val textShadowOffsetYDip: Float,
        val textShadowStrokeWidthDip: Float,
        val textShadowBlurRadiusDip: Float,
        val textShadowColor: Color,

        val selectionColor: Color,

        val imageSourceScale: Float,
        val imageScaleFiltered: Boolean,

        val fontCollection: FontCollection
)

fun appContentConfig(context: Context, settings: UserSettings, fontCollection: FontCollection) = ContentConfig(
        context.displayMetrics.density,

        defaultLocale = defaultLocale(context, settings),
        ignoreDeclaredLocale = settings[AnalyzeKeys.ignoreDeclaredLanguage],

        firstLineIndentEm = settings[FormatKeys.firstLineIndentEm],
        textAlign = settings[FormatKeys.textAlign],
        letterSpacingEm = settings[FormatKeys.letterSpacingEm],
        wordSpacingMultiplier = settings[FormatKeys.wordSpacingMultiplier],
        lineHeightMultiplier = settings[FormatKeys.lineHeightMultiplier],
        paragraphVerticalMarginEm = settings[FormatKeys.paragraphVerticalMarginEm],
        hangingConfig = if (settings[FormatKeys.hangingPunctuation]) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings[FormatKeys.hyphenation],

        textFontFamily = settings[FormatKeys.textFontFamily],
        textFontStyle = settings[FormatKeys.textFontStyle],

        textSizeDip = settings[FormatKeys.textSizeDip],
        textScaleX = settings[FormatKeys.textScaleX],
        textSkewX = settings[FormatKeys.textSkewX],
        textStrokeWidthDip = settings[FormatKeys.textStrokeWidthDip],
        textColor = Color(settings[FormatKeys.textColor]),
        textAntialiasing = settings[FormatKeys.textAntialiasing],
        textHinting = settings[FormatKeys.textHinting],
        textSubpixelPositioning = settings[FormatKeys.textSubpixelPositioning],

        textShadowEnabled = settings[FormatKeys.textShadowEnabled],
        textShadowOffsetXDip = settings[FormatKeys.textShadowOffsetXDip],
        textShadowOffsetYDip = settings[FormatKeys.textShadowOffsetYDip],
        textShadowStrokeWidthDip = settings[FormatKeys.textShadowStrokeWidthDip],
        textShadowBlurRadiusDip = settings[FormatKeys.textShadowBlurRadiusDip],
        textShadowColor = Color(settings[FormatKeys.textShadowColor]),

        selectionColor = Color(settings[UIKeys.selectionColor]),

        imageSourceScale = if (settings[ImageKeys.sourceScaleByDpi]) context.displayMetrics.density else settings[ImageKeys.sourceScale],
        imageScaleFiltered = settings[ImageKeys.scaleFiltered],

        fontCollection = fontCollection
)

private fun defaultLocale(context: Context, settings: UserSettings) =
        if (settings[AnalyzeKeys.defaultLanguageIsSystem]) {
            systemLocale(context)
        } else {
            Locale(settings[AnalyzeKeys.defaultLanguage])
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)