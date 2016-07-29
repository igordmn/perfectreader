package com.dmi.perfectreader.data

import com.dmi.perfectreader.fragment.book.content.obj.param.TextAlign
import com.dmi.perfectreader.fragment.bookcontrol.entity.*
import com.dmi.util.graphic.Color
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
        val firstLineIndent by key(20F)
        val textAlign by key(TextAlign.JUSTIFY)
        val fontSizeMultiplier by key(1F)
        val lineHeightMultiplier by key(1F)
        val paragraphVerticalMarginMultiplier by key(1F)
        val hangingPunctuation by key(true)
        val hyphenation by key(true)

        val pageMarginLeft by key(20F)
        val pageMarginRight by key(20F)
        val pageMarginTop by key(20F)
        val pageMarginBottom by key(20F)
    }

    object Image : Keys(this) {
        val sourceScaleByDpi by key(true)
        val sourceScale by key(1F)
        val scaleFiltered by key(true)
    }

    object Control : Keys(this) {
        object TapZones : Keys(this) {
            interface TapZoneActions {
                val center: EnumKey<Action>
                val top: EnumKey<Action>
                val bottom: EnumKey<Action>
                val left: EnumKey<Action>
                val right: EnumKey<Action>
                val topLeft: EnumKey<Action>
                val topRight: EnumKey<Action>
                val bottomLeft: EnumKey<Action>
                val bottomRight: EnumKey<Action>

                operator fun get(tapZone: TapZone) = when (tapZone) {
                    TapZone.CENTER -> center
                    TapZone.TOP -> top
                    TapZone.BOTTOM -> bottom
                    TapZone.LEFT -> left
                    TapZone.RIGHT -> right
                    TapZone.TOP_LEFT -> topLeft
                    TapZone.TOP_RIGHT -> topRight
                    TapZone.BOTTOM_LEFT -> bottomLeft
                    TapZone.BOTTOM_RIGHT -> bottomRight
                }
            }

            val interactiveClickMode by key(InteractiveClickMode.CLICK)

            object ShortTaps : Keys(this) {
                val configuration by key(TapZoneConfiguration.NINE)

                object Actions : Keys(this), TapZoneActions {
                    override val center by key(Action.TOGGLE_MENU)
                    override val top by key(Action.TOGGLE_MENU)
                    override val bottom by key(Action.TOGGLE_MENU)
                    override val left by key(Action.GO_PREVIOUS_PAGE)
                    override val right by key(Action.GO_NEXT_PAGE)
                    override val topLeft by key(Action.GO_PREVIOUS_PAGE)
                    override val topRight by key(Action.GO_NEXT_PAGE)
                    override val bottomLeft by key(Action.GO_PREVIOUS_PAGE)
                    override val bottomRight by key(Action.GO_NEXT_PAGE)
                }
            }

            object LongTaps : Keys(this) {
                val configuration by key(TapZoneConfiguration.SINGLE)
                val timeout by key(500L)

                object Actions : Keys(this), TapZoneActions {
                    override val center by key(Action.SELECT_TEXT)
                    override val top by key(Action.SELECT_TEXT)
                    override val bottom by key(Action.SELECT_TEXT)
                    override val left by key(Action.SELECT_TEXT)
                    override val right by key(Action.SELECT_TEXT)
                    override val topLeft by key(Action.SELECT_TEXT)
                    override val topRight by key(Action.SELECT_TEXT)
                    override val bottomLeft by key(Action.SELECT_TEXT)
                    override val bottomRight by key(Action.SELECT_TEXT)
                }
            }

            object DoubleTaps : Keys(this) {
                val configuration by key(TapZoneConfiguration.SINGLE)
                val timeout by key(300L)

                object Actions : Keys(this), TapZoneActions {
                    override val center by key(Action.NONE)
                    override val top by key(Action.NONE)
                    override val bottom by key(Action.NONE)
                    override val left by key(Action.NONE)
                    override val right by key(Action.NONE)
                    override val topLeft by key(Action.NONE)
                    override val topRight by key(Action.NONE)
                    override val bottomLeft by key(Action.NONE)
                    override val bottomRight by key(Action.NONE)
                }
            }
        }

        object HardKeys : Keys(this) {
            interface KeyActions {
                val volumeUp: EnumKey<Action>
                val volumeDown: EnumKey<Action>
                val menu: EnumKey<Action>
                val back: EnumKey<Action>
                val search: EnumKey<Action>
                val camera: EnumKey<Action>
                val trackballPress: EnumKey<Action>
                val trackballLeft: EnumKey<Action>
                val trackballRight: EnumKey<Action>
                val trackballUp: EnumKey<Action>
                val trackballDown: EnumKey<Action>

                operator fun get(hardKey: HardKey) = when (hardKey) {
                    HardKey.VOLUME_UP -> volumeUp
                    HardKey.VOLUME_DOWN -> volumeDown
                    HardKey.MENU -> menu
                    HardKey.BACK -> back
                    HardKey.SEARCH -> search
                    HardKey.CAMERA -> camera
                    HardKey.TRACKBALL_PRESS -> trackballPress
                    HardKey.TRACKBALL_LEFT -> trackballLeft
                    HardKey.TRACKBALL_RIGHT -> trackballRight
                    HardKey.TRACKBALL_UP -> trackballUp
                    HardKey.TRACKBALL_DOWN -> trackballDown
                }
            }

            object ShortPress : Keys(this) {
                object Actions : Keys(this), KeyActions {
                    override val volumeUp by key(Action.GO_PREVIOUS_PAGE)
                    override val volumeDown by key(Action.GO_NEXT_PAGE)
                    override val menu by key(Action.TOGGLE_MENU)
                    override val back by key(Action.EXIT_APP)
                    override val search by key(Action.NONE)
                    override val camera by key(Action.NONE)
                    override val trackballPress by key(Action.NONE)
                    override val trackballLeft by key(Action.NONE)
                    override val trackballRight by key(Action.NONE)
                    override val trackballUp by key(Action.NONE)
                    override val trackballDown by key(Action.NONE)
                }
            }

            object LongPress : Keys(this) {
                val timeout by key(500L)

                object Actions : Keys(this), KeyActions {
                    override val volumeUp by key(Action.GO_PREVIOUS_PAGE)
                    override val volumeDown by key(Action.GO_NEXT_PAGE)
                    override val menu by key(Action.NONE)
                    override val back by key(Action.NONE)
                    override val search by key(Action.NONE)
                    override val camera by key(Action.NONE)
                    override val trackballPress by key(Action.NONE)
                    override val trackballLeft by key(Action.NONE)
                    override val trackballRight by key(Action.NONE)
                    override val trackballUp by key(Action.NONE)
                    override val trackballDown by key(Action.NONE)
                }
            }

            object DoublePress : Keys(this) {
                val timeout by key(300L)

                object Actions : Keys(this), KeyActions {
                    override val volumeUp by key(Action.NONE)
                    override val volumeDown by key(Action.NONE)
                    override val menu by key(Action.NONE)
                    override val back by key(Action.NONE)
                    override val search by key(Action.NONE)
                    override val camera by key(Action.NONE)
                    override val trackballPress by key(Action.NONE)
                    override val trackballLeft by key(Action.NONE)
                    override val trackballRight by key(Action.NONE)
                    override val trackballUp by key(Action.NONE)
                    override val trackballDown by key(Action.NONE)
                }
            }
        }
    }

    object UI : Keys(this) {
        val selectionBackgroundColor by key(Color(255, 174, 223, 240).value)
        val selectionTextColor by key(Color.WHITE.value)
    }
}