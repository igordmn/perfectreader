package com.dmi.perfectreader.setting

import android.content.Context
import com.dmi.perfectreader.bookcontrol.*
import com.dmi.perfectreader.layout.layoutobj.common.TextAlign
import com.dmi.util.setting.AbstractSettings
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AppSettings
@Inject
constructor(@Named("applicationContext") protected var context: Context) : AbstractSettings() {
    val format = Format()

    inner class Format {
        val textAlign: AbstractSettings.Setting<TextAlign> = setting("format.textAlign", TextAlign.JUSTIFY)
        val fontSizePercents: AbstractSettings.Setting<Int> = setting("format.fontSizePercents", 100)
        val lineHeightPercents: AbstractSettings.Setting<Int> = setting("format.lineHeightPercents", 100)
        val hangingPunctuation: AbstractSettings.Setting<Boolean> = setting("format.hangingPunctuation", true)
        val hyphenation: AbstractSettings.Setting<Boolean> = setting("format.hyphenation", true)
    }

    val control = Control()

    inner class Control {
        val tapZones = TapZones()

        inner class TapZones {
            var shortTaps = ShortTaps()

            inner class ShortTaps {
                val configuration: AbstractSettings.Setting<TapZoneConfiguration> = setting("control.tapZones.shortTaps.configuration", TapZoneConfiguration.NINE)
                private val centerAction = setting("control.tapZones.shortTaps.centerAction", Action.TOGGLE_MENU)
                private val topAction = setting("control.tapZones.shortTaps.topAction", Action.TOGGLE_MENU)
                private val bottomAction = setting("control.tapZones.shortTaps.bottomAction", Action.TOGGLE_MENU)
                private val leftAction = setting("control.tapZones.shortTaps.leftAction", Action.GO_PREVIEW_PAGE)
                private val rightAction = setting("control.tapZones.shortTaps.rightAction", Action.GO_NEXT_PAGE)
                private val topLeftAction = setting("control.tapZones.shortTaps.topLeftAction", Action.GO_PREVIEW_PAGE)
                private val topRightAction = setting("control.tapZones.shortTaps.topRightAction", Action.GO_NEXT_PAGE)
                private val bottomLeftAction = setting("control.tapZones.shortTaps.bottomLeftAction", Action.GO_PREVIEW_PAGE)
                private val bottomRightAction = setting("control.tapZones.shortTaps.bottomRightAction", Action.GO_NEXT_PAGE)

                fun action(tapZone: TapZone): AbstractSettings.Setting<Action> {
                    when (tapZone) {
                        TapZone.CENTER -> return centerAction
                        TapZone.TOP -> return topAction
                        TapZone.BOTTOM -> return bottomAction
                        TapZone.LEFT -> return leftAction
                        TapZone.RIGHT -> return rightAction
                        TapZone.TOP_LEFT -> return topLeftAction
                        TapZone.TOP_RIGHT -> return topRightAction
                        TapZone.BOTTOM_LEFT -> return bottomLeftAction
                        TapZone.BOTTOM_RIGHT -> return bottomRightAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            var longTaps = LongTaps()

            inner class LongTaps {
                val configuration: AbstractSettings.Setting<TapZoneConfiguration> = setting("control.tapZones.longTaps.configuration", TapZoneConfiguration.SINGLE)
                val timeout: AbstractSettings.Setting<Long> = setting("control.tapZones.longTaps.timeout", 500L)
                private val centerAction = setting("control.tapZones.longTaps.centerAction", Action.SELECT_TEXT)
                private val topAction = setting("control.tapZones.longTaps.topAction", Action.SELECT_TEXT)
                private val bottomAction = setting("control.tapZones.longTaps.bottomAction", Action.SELECT_TEXT)
                private val leftAction = setting("control.tapZones.longTaps.leftAction", Action.SELECT_TEXT)
                private val rightAction = setting("control.tapZones.longTaps.rightAction", Action.SELECT_TEXT)
                private val topLeftAction = setting("control.tapZones.longTaps.topLeftAction", Action.SELECT_TEXT)
                private val topRightAction = setting("control.tapZones.longTaps.topRightAction", Action.SELECT_TEXT)
                private val bottomLeftAction = setting("control.tapZones.longTaps.bottomLeftAction", Action.SELECT_TEXT)
                private val bottomRightAction = setting("control.tapZones.longTaps.bottomRightAction", Action.SELECT_TEXT)

                fun action(tapZone: TapZone): AbstractSettings.Setting<Action> {
                    when (tapZone) {
                        TapZone.CENTER -> return centerAction
                        TapZone.TOP -> return topAction
                        TapZone.BOTTOM -> return bottomAction
                        TapZone.LEFT -> return leftAction
                        TapZone.RIGHT -> return rightAction
                        TapZone.TOP_LEFT -> return topLeftAction
                        TapZone.TOP_RIGHT -> return topRightAction
                        TapZone.BOTTOM_LEFT -> return bottomLeftAction
                        TapZone.BOTTOM_RIGHT -> return bottomRightAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            var doubleTaps = DoubleTaps()

            inner class DoubleTaps {
                val configuration: AbstractSettings.Setting<TapZoneConfiguration> = setting("control.tapZones.doubleTaps.configuration", TapZoneConfiguration.SINGLE)
                val timeout: AbstractSettings.Setting<Long> = setting("control.tapZones.doubleTaps.timeout", 300L)
                private val centerAction = setting("control.tapZones.doubleTaps.centerAction", Action.NONE)
                private val topAction = setting("control.tapZones.doubleTaps.topAction", Action.NONE)
                private val bottomAction = setting("control.tapZones.doubleTaps.bottomAction", Action.NONE)
                private val leftAction = setting("control.tapZones.doubleTaps.leftAction", Action.NONE)
                private val rightAction = setting("control.tapZones.doubleTaps.rightAction", Action.NONE)
                private val topLeftAction = setting("control.tapZones.doubleTaps.topLeftAction", Action.NONE)
                private val topRightAction = setting("control.tapZones.doubleTaps.topRightAction", Action.NONE)
                private val bottomLeftAction = setting("control.tapZones.doubleTaps.bottomLeftAction", Action.NONE)
                private val bottomRightAction = setting("control.tapZones.doubleTaps.bottomRightAction", Action.NONE)

                fun action(tapZone: TapZone): AbstractSettings.Setting<Action> {
                    when (tapZone) {
                        TapZone.CENTER -> return centerAction
                        TapZone.TOP -> return topAction
                        TapZone.BOTTOM -> return bottomAction
                        TapZone.LEFT -> return leftAction
                        TapZone.RIGHT -> return rightAction
                        TapZone.TOP_LEFT -> return topLeftAction
                        TapZone.TOP_RIGHT -> return topRightAction
                        TapZone.BOTTOM_LEFT -> return bottomLeftAction
                        TapZone.BOTTOM_RIGHT -> return bottomRightAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            private val interactiveClickMode = setting("control.tapZones.interactiveClickMode", InteractiveClickMode.CLICK)
        }

        val hardKeys = HardKeys()

        inner class HardKeys {
            var shortPress = ShortPress()

            inner class ShortPress {
                private val volumeUpAction = setting("control.hardKeys.shortPress.volumeUpAction", Action.GO_PREVIEW_PAGE)
                private val volumeDownAction = setting("control.hardKeys.shortPress.volumeDownAction", Action.GO_NEXT_PAGE)
                private val menuAction = setting("control.hardKeys.shortPress.menuAction", Action.TOGGLE_MENU)
                private val backAction = setting("control.hardKeys.shortPress.backAction", Action.EXIT)
                private val searchAction = setting("control.hardKeys.shortPress.searchAction", Action.NONE)
                private val cameraAction = setting("control.hardKeys.shortPress.cameraAction", Action.NONE)
                private val trackballPressAction = setting("control.hardKeys.shortPress.trackballPressAction", Action.NONE)
                private val trackballLeftAction = setting("control.hardKeys.shortPress.trackballLeftAction", Action.NONE)
                private val trackballRightAction = setting("control.hardKeys.shortPress.trackballRightAction", Action.NONE)
                private val trackballUpAction = setting("control.hardKeys.shortPress.trackballUpAction", Action.NONE)
                private val trackballDownAction = setting("control.hardKeys.shortPress.trackballDownAction", Action.NONE)

                fun action(hardKey: HardKey): AbstractSettings.Setting<Action> {
                    when (hardKey) {
                        HardKey.VOLUME_UP -> return volumeUpAction
                        HardKey.VOLUME_DOWN -> return volumeDownAction
                        HardKey.MENU -> return menuAction
                        HardKey.BACK -> return backAction
                        HardKey.SEARCH -> return searchAction
                        HardKey.CAMERA -> return cameraAction
                        HardKey.TRACKBALL_PRESS -> return trackballPressAction
                        HardKey.TRACKBALL_LEFT -> return trackballLeftAction
                        HardKey.TRACKBALL_RIGHT -> return trackballRightAction
                        HardKey.TRACKBALL_UP -> return trackballUpAction
                        HardKey.TRACKBALL_DOWN -> return trackballDownAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            var longPress = LongPress()

            inner class LongPress {
                val timeout: AbstractSettings.Setting<Long> = setting("control.hardKeys.longPress.timeout", 500L)
                private val volumeUpAction = setting("control.hardKeys.longPress.volumeUpAction", Action.GO_PREVIEW_PAGE)
                private val volumeDownAction = setting("control.hardKeys.longPress.volumeDownAction", Action.GO_NEXT_PAGE)
                private val menuAction = setting("control.hardKeys.longPress.menuAction", Action.NONE)
                private val backAction = setting("control.hardKeys.longPress.backAction", Action.NONE)
                private val searchAction = setting("control.hardKeys.longPress.searchAction", Action.NONE)
                private val cameraAction = setting("control.hardKeys.longPress.cameraAction", Action.NONE)
                private val trackballPressAction = setting("control.hardKeys.longPress.trackballPressAction", Action.NONE)
                private val trackballLeftAction = setting("control.hardKeys.longPress.trackballLeftAction", Action.NONE)
                private val trackballRightAction = setting("control.hardKeys.longPress.trackballRightAction", Action.NONE)
                private val trackballUpAction = setting("control.hardKeys.longPress.trackballUpAction", Action.NONE)
                private val trackballDownAction = setting("control.hardKeys.longPress.trackballDownAction", Action.NONE)

                fun action(hardKey: HardKey): AbstractSettings.Setting<Action> {
                    when (hardKey) {
                        HardKey.VOLUME_UP -> return volumeUpAction
                        HardKey.VOLUME_DOWN -> return volumeDownAction
                        HardKey.MENU -> return menuAction
                        HardKey.BACK -> return backAction
                        HardKey.SEARCH -> return searchAction
                        HardKey.CAMERA -> return cameraAction
                        HardKey.TRACKBALL_PRESS -> return trackballPressAction
                        HardKey.TRACKBALL_LEFT -> return trackballLeftAction
                        HardKey.TRACKBALL_RIGHT -> return trackballRightAction
                        HardKey.TRACKBALL_UP -> return trackballUpAction
                        HardKey.TRACKBALL_DOWN -> return trackballDownAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            var doublePress = DoublePress()

            inner class DoublePress {
                val timeout: AbstractSettings.Setting<Long> = setting("control.hardKeys.doublePress.timeout", 300L)
                private val volumeUpAction = setting("control.hardKeys.doublePress.volumeUpAction", Action.NONE)
                private val volumeDownAction = setting("control.hardKeys.doublePress.volumeDownAction", Action.NONE)
                private val menuAction = setting("control.hardKeys.doublePress.menuAction", Action.NONE)
                private val backAction = setting("control.hardKeys.doublePress.backAction", Action.NONE)
                private val searchAction = setting("control.hardKeys.doublePress.searchAction", Action.NONE)
                private val cameraAction = setting("control.hardKeys.doublePress.cameraAction", Action.NONE)
                private val trackballPressAction = setting("control.hardKeys.doublePress.trackballPressAction", Action.NONE)
                private val trackballLeftAction = setting("control.hardKeys.doublePress.trackballLeftAction", Action.NONE)
                private val trackballRightAction = setting("control.hardKeys.doublePress.trackballRightAction", Action.NONE)
                private val trackballUpAction = setting("control.hardKeys.doublePress.trackballUpAction", Action.NONE)
                private val trackballDownAction = setting("control.hardKeys.doublePress.trackballDownAction", Action.NONE)

                fun action(hardKey: HardKey): AbstractSettings.Setting<Action> {
                    when (hardKey) {
                        HardKey.VOLUME_UP -> return volumeUpAction
                        HardKey.VOLUME_DOWN -> return volumeDownAction
                        HardKey.MENU -> return menuAction
                        HardKey.BACK -> return backAction
                        HardKey.SEARCH -> return searchAction
                        HardKey.CAMERA -> return cameraAction
                        HardKey.TRACKBALL_PRESS -> return trackballPressAction
                        HardKey.TRACKBALL_LEFT -> return trackballLeftAction
                        HardKey.TRACKBALL_RIGHT -> return trackballRightAction
                        HardKey.TRACKBALL_UP -> return trackballUpAction
                        HardKey.TRACKBALL_DOWN -> return trackballDownAction
                        else -> throw IllegalArgumentException()
                    }
                }
            }
        }
    }

    init {
        init(context, "settings")
    }
}