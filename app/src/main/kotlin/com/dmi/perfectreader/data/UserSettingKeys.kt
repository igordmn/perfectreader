package com.dmi.perfectreader.data

import com.dmi.perfectreader.fragment.book.content.obj.param.TextAlign
import com.dmi.perfectreader.fragment.control.OpenTapMode
import com.dmi.perfectreader.fragment.reader.action.ReaderActionID
import com.dmi.util.action.TouchZone
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.graphic.Color
import com.dmi.util.input.HardKey
import com.dmi.util.setting.Settings.EnumKey
import com.dmi.util.setting.Settings.Keys

object UserSettingKeys : Keys() {
    object Analyze : Keys(this) {
        val defaultCharsetIsAuto by key(true)
        val defaultCharset by key("")
        val ignoreDeclaredCharset by key(false)
        val defaultLanguageIsSystem by key(true)
        val defaultLanguage by key("")
        val ignoreDeclaredLanguage by key(false)
    }

    object Format : Keys(this) {
        val firstLineIndentEm by key(1F)
        val textAlign by key(TextAlign.JUSTIFY)
        val letterSpacingEm by key(0F)
        val wordSpacingMultiplier by key(1F)
        val lineHeightMultiplier by key(1F)
        val paragraphVerticalMarginEm by key(0.5F)
        val hangingPunctuation by key(true)
        val hyphenation by key(true)

        val textFontFamily by key("")
        val textFontStyle by key("Regular")
        val textSizeDip by key(20F)
        val textScaleX by key(1.0F)
        val textSkewX by key(0.0F)
        val textStrokeWidthDip by key(0.0F)
        val textColor by key(Color.BLACK.value)
        val textAntialiasing by key(true)
        val textHinting by key(true)
        val textSubpixelPositioning by key(true)

        val textShadowEnabled by key(false)
        val textShadowOffsetXDip by key(0F)
        val textShadowOffsetYDip by key(0F)
        val textShadowStrokeWidthDip by key(0F)
        val textShadowBlurRadiusDip by key(1F)
        val textShadowColor by key(Color.GRAY.value)

        val pageTextGammaCorrection by key(1F)
        val pagePaddingLeftDip by key(20F)
        val pagePaddingRightDip by key(20F)
        val pagePaddingTopDip by key(20F)
        val pagePaddingBottomDip by key(20F)
    }

    object Navigation : Keys(this) {
        val pageSymbolCount by key(1024)
        val pageSymbolCountIsAuto by key(true)
    }

    object System : Keys(this) {
        val fontsPath by key("externalStorage://Fonts")
    }

    object Image : Keys(this) {
        val sourceScaleByDpi by key(true)
        val sourceScale by key(1F)
        val scaleFiltered by key(true)
    }

    object Control : Keys(this) {
        object Touches : Keys(this) {
            val linkOpenMode by key(OpenTapMode.SINGLE_TAP)
            val imageOpenMode by key(OpenTapMode.SINGLE_TAP)
            val doubleTapEnabled by key(false)
            val tapMaxOffset by key(8F)
            val longTapTimeout by key(500L)
            val doubleTapTimeout by key(300L)

            abstract class Taps(
                    configuration: TouchZoneConfiguration,
                    left: ReaderActionID,
                    middle: ReaderActionID,
                    right: ReaderActionID
            ) : Keys(this) {
                constructor(configuration: TouchZoneConfiguration, middle: ReaderActionID) : this(configuration, middle, middle, middle)
                constructor(configuration: TouchZoneConfiguration) : this(configuration, ReaderActionID.NONE)

                val configuration by key(configuration)

                val topLeft by key(left)
                val topMiddle1 by key(left)
                val topMiddle2 by key(left)
                val topRight by key(right)

                val middle1Left by key(left)
                val middle1Middle1 by key(middle)
                val middle1Middle2 by key(middle)
                val middle1Right by key(right)

                val middle2Left by key(left)
                val middle2Middle1 by key(middle)
                val middle2Middle2 by key(middle)
                val middle2Right by key(right)

                val bottomLeft by key(left)
                val bottomMiddle1 by key(right)
                val bottomMiddle2 by key(right)
                val bottomRight by key(right)

                operator fun get(zone: TouchZone): EnumKey<ReaderActionID> = when (zone) {
                    TouchZone.TOP_LEFT -> topLeft
                    TouchZone.TOP_MIDDLE1 -> topMiddle1
                    TouchZone.TOP_MIDDLE2 -> topMiddle2
                    TouchZone.TOP_RIGHT -> topRight
                    TouchZone.MIDDLE1_LEFT -> middle1Left
                    TouchZone.MIDDLE1_MIDDLE1 -> middle1Middle1
                    TouchZone.MIDDLE1_MIDDLE2 -> middle1Middle2
                    TouchZone.MIDDLE1_RIGHT -> middle1Right
                    TouchZone.MIDDLE2_LEFT -> middle2Left
                    TouchZone.MIDDLE2_MIDDLE1 -> middle2Middle1
                    TouchZone.MIDDLE2_MIDDLE2 -> middle2Middle2
                    TouchZone.MIDDLE2_RIGHT -> middle2Right
                    TouchZone.BOTTOM_LEFT -> bottomLeft
                    TouchZone.BOTTOM_MIDDLE1 -> bottomMiddle1
                    TouchZone.BOTTOM_MIDDLE2 -> bottomMiddle2
                    TouchZone.BOTTOM_RIGHT -> bottomRight
                }
            }

            abstract class Scrolls(
                    configuration: TouchZoneConfiguration,
                    first: ReaderActionID,
                    middle: ReaderActionID,
                    last: ReaderActionID
            ) : Keys(this) {
                constructor(configuration: TouchZoneConfiguration, middle: ReaderActionID) : this(configuration, middle, middle, middle)
                constructor(configuration: TouchZoneConfiguration) : this(configuration, ReaderActionID.NONE)

                val configuration by key(configuration)

                val first by key(first)
                val middle1 by key(middle)
                val middle2 by key(middle)
                val last by key(last)

                fun getVertical(zone: TouchZone): EnumKey<ReaderActionID> = when (zone) {
                    TouchZone.TOP_LEFT -> first
                    TouchZone.TOP_MIDDLE1 -> middle1
                    TouchZone.TOP_MIDDLE2 -> middle2
                    TouchZone.TOP_RIGHT -> last
                    TouchZone.MIDDLE1_LEFT -> first
                    TouchZone.MIDDLE1_MIDDLE1 -> middle1
                    TouchZone.MIDDLE1_MIDDLE2 -> middle2
                    TouchZone.MIDDLE1_RIGHT -> last
                    TouchZone.MIDDLE2_LEFT -> first
                    TouchZone.MIDDLE2_MIDDLE1 -> middle1
                    TouchZone.MIDDLE2_MIDDLE2 -> middle2
                    TouchZone.MIDDLE2_RIGHT -> last
                    TouchZone.BOTTOM_LEFT -> first
                    TouchZone.BOTTOM_MIDDLE1 -> middle1
                    TouchZone.BOTTOM_MIDDLE2 -> middle2
                    TouchZone.BOTTOM_RIGHT -> last
                }

                fun getHorizontal(zone: TouchZone): EnumKey<ReaderActionID> = when (zone) {
                    TouchZone.TOP_LEFT -> first
                    TouchZone.TOP_MIDDLE1 -> first
                    TouchZone.TOP_MIDDLE2 -> first
                    TouchZone.TOP_RIGHT -> first
                    TouchZone.MIDDLE1_LEFT -> middle1
                    TouchZone.MIDDLE1_MIDDLE1 -> middle1
                    TouchZone.MIDDLE1_MIDDLE2 -> middle1
                    TouchZone.MIDDLE1_RIGHT -> middle1
                    TouchZone.MIDDLE2_LEFT -> middle2
                    TouchZone.MIDDLE2_MIDDLE1 -> middle2
                    TouchZone.MIDDLE2_MIDDLE2 -> middle2
                    TouchZone.MIDDLE2_RIGHT -> middle2
                    TouchZone.BOTTOM_LEFT -> last
                    TouchZone.BOTTOM_MIDDLE1 -> last
                    TouchZone.BOTTOM_MIDDLE2 -> last
                    TouchZone.BOTTOM_RIGHT -> last
                }
            }

            object SingleTaps : Taps(
                    TouchZoneConfiguration.NINE,
                    left = ReaderActionID.GO_PREVIOUS_PAGE,
                    middle = ReaderActionID.TOGGLE_MENU,
                    right = ReaderActionID.GO_NEXT_PAGE
            )

            object LongTaps : Taps(TouchZoneConfiguration.SINGLE, ReaderActionID.SELECT_WORD)
            object DoubleTaps : Taps(TouchZoneConfiguration.SINGLE, ReaderActionID.NONE)
            object TwoFingersSingleTaps : Taps(TouchZoneConfiguration.SINGLE)
            object TwoFingersLongTaps : Taps(TouchZoneConfiguration.SINGLE)
            object TwoFingersDoubleTaps : Taps(TouchZoneConfiguration.SINGLE)

            object LeftScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.GO_NEXT_PAGE)
            object RightScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.GO_PREVIOUS_PAGE)

            object UpScrolls : Scrolls(TouchZoneConfiguration.THREE_COLUMNS_FIXED,
                    ReaderActionID.CHANGE_SCREEN_BRIGHTNESS,
                    ReaderActionID.SCROLL_PREVIOUS_PAGE,
                    ReaderActionID.SCROLL_PREVIOUS_PAGE
            )

            object DownScrolls : Scrolls(TouchZoneConfiguration.THREE_COLUMNS_FIXED,
                    ReaderActionID.CHANGE_SCREEN_BRIGHTNESS,
                    ReaderActionID.SCROLL_NEXT_PAGE,
                    ReaderActionID.SCROLL_NEXT_PAGE
            )

            object TwoFingersLeftScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.GO_NEXT_PAGE_10)
            object TwoFingersRightScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.GO_PREVIOUS_PAGE_10)
            object TwoFingersUpScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.CHANGE_TEXT_LINE_HEIGHT)
            object TwoFingersDownScrolls : Scrolls(TouchZoneConfiguration.SINGLE, ReaderActionID.CHANGE_TEXT_LINE_HEIGHT)

            object TwoFingersPinches : Keys(this) {
                val pinchIn by key(ReaderActionID.CHANGE_TEXT_SIZE)
                val pinchOut by key(ReaderActionID.CHANGE_TEXT_SIZE)
            }
        }

        object HardKeys : Keys(this) {
            val doubleTapEnabled by key(false)
            val longTapTimeout by key(500L)
            val doubleTapTimeout by key(300L)

            object SinglePress : Keys(this) {
                val volumeUp by key(ReaderActionID.GO_PREVIOUS_PAGE)
                val volumeDown by key(ReaderActionID.GO_NEXT_PAGE)
                val menu by key(ReaderActionID.TOGGLE_MENU)
                val back by key(ReaderActionID.EXIT_ACTIVITY)
                val search by key(ReaderActionID.NONE)
                val camera by key(ReaderActionID.NONE)
                val dpadPress by key(ReaderActionID.NONE)
                val dpadLeft by key(ReaderActionID.NONE)
                val dpadRight by key(ReaderActionID.NONE)
                val dpadUp by key(ReaderActionID.NONE)
                val dpadDown by key(ReaderActionID.NONE)

                operator fun get(hardKey: HardKey) = when (hardKey) {
                    HardKey.VOLUME_UP -> volumeUp
                    HardKey.VOLUME_DOWN -> volumeDown
                    HardKey.MENU -> menu
                    HardKey.BACK -> back
                    HardKey.SEARCH -> search
                    HardKey.CAMERA -> camera
                    HardKey.DPAD_CENTER -> dpadPress
                    HardKey.DPAD_LEFT -> dpadLeft
                    HardKey.DPAD_RIGHT -> dpadRight
                    HardKey.DPAD_UP -> dpadUp
                    HardKey.DPAD_DOWN -> dpadDown
                }
            }
        }
    }

    object UI : Keys(this) {
        val selectionColor by key(Color(255, 174, 223, 240).value)
        val selectionSelectWords by key(false)
    }
}