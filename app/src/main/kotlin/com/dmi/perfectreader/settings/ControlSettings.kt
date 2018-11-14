package com.dmi.perfectreader.settings

import com.dmi.perfectreader.ui.action.ActionID
import com.dmi.util.action.TouchZone
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.input.HardKey
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.group
import com.dmi.util.persist.value
import kotlin.reflect.KMutableProperty0

class ControlSettings(store: ValueStore) {
    val touches by store.group(::Touches)
    val hardKeys by store.group(::HardKeys)

    class Touches(store: ValueStore) {
        var doubleTapEnabled by store.value(false)
        var tapMaxOffset by store.value(0F)
        var longTapTimeout by store.value(500L)
        var doubleTapTimeout by store.value(300L)

        val singleTaps by store.group(::SingleTaps)
        val longTaps by store.group(::LongTaps)
        val doubleTaps by store.group(::DoubleTaps)
        val twoFingersSingleTaps by store.group(::TwoFingersSingleTaps)
        val twoFingersLongTaps by store.group(::TwoFingersLongTaps)
        val twoFingersDoubleTaps by store.group(::TwoFingersDoubleTaps)
        val leftScrolls by store.group(::LeftScrolls)
        val rightScrolls by store.group(::RightScrolls)
        val upScrolls by store.group(::UpScrolls)
        val downScrolls by store.group(::DownScrolls)
        val twoFingersLeftScrolls by store.group(::TwoFingersLeftScrolls)
        val twoFingersRightScrolls by store.group(::TwoFingersRightScrolls)
        val twoFingersUpScrolls by store.group(::TwoFingersUpScrolls)
        val twoFingersDownScrolls by store.group(::TwoFingersDownScrolls)
        val twoFingersPinches by store.group(::TwoFingersPinches)

        abstract class Taps(
                store: ValueStore,
                configuration: TouchZoneConfiguration,
                left: ActionID,
                middle: ActionID,
                right: ActionID
        ) {
            constructor(
                    store: ValueStore,
                    configuration: TouchZoneConfiguration,
                    middle: ActionID
            ) : this(store, configuration, middle, middle, middle)

            constructor(
                    store: ValueStore,
                    configuration: TouchZoneConfiguration
            ) : this(store, configuration, ActionID.NONE)

            var configuration by store.value(configuration)

            var topLeft by store.value(left)
            var topMiddle1 by store.value(middle)
            var topMiddle2 by store.value(middle)
            var topRight by store.value(right)

            var middle1Left by store.value(left)
            var middle1Middle1 by store.value(middle)
            var middle1Middle2 by store.value(middle)
            var middle1Right by store.value(right)

            var middle2Left by store.value(left)
            var middle2Middle1 by store.value(middle)
            var middle2Middle2 by store.value(middle)
            var middle2Right by store.value(right)

            var bottomLeft by store.value(left)
            var bottomMiddle1 by store.value(middle)
            var bottomMiddle2 by store.value(middle)
            var bottomRight by store.value(right)

            fun property(zone: TouchZone): KMutableProperty0<ActionID> = when (zone) {
                TouchZone.TOP_LEFT -> ::topLeft
                TouchZone.TOP_MIDDLE1 -> ::topMiddle1
                TouchZone.TOP_MIDDLE2 -> ::topMiddle2
                TouchZone.TOP_RIGHT -> ::topRight
                TouchZone.MIDDLE1_LEFT -> ::middle1Left
                TouchZone.MIDDLE1_MIDDLE1 -> ::middle1Middle1
                TouchZone.MIDDLE1_MIDDLE2 -> ::middle1Middle2
                TouchZone.MIDDLE1_RIGHT -> ::middle1Right
                TouchZone.MIDDLE2_LEFT -> ::middle2Left
                TouchZone.MIDDLE2_MIDDLE1 -> ::middle2Middle1
                TouchZone.MIDDLE2_MIDDLE2 -> ::middle2Middle2
                TouchZone.MIDDLE2_RIGHT -> ::middle2Right
                TouchZone.BOTTOM_LEFT -> ::bottomLeft
                TouchZone.BOTTOM_MIDDLE1 -> ::bottomMiddle1
                TouchZone.BOTTOM_MIDDLE2 -> ::bottomMiddle2
                TouchZone.BOTTOM_RIGHT -> ::bottomRight
            }
        }

        abstract class Scrolls(
                store: ValueStore,
                configuration: TouchZoneConfiguration,
                first: ActionID,
                middle: ActionID,
                last: ActionID
        ) {
            constructor(
                    store: ValueStore,
                    configuration: TouchZoneConfiguration,
                    middle: ActionID
            ) : this(store, configuration, middle, middle, middle)

            constructor(
                    store: ValueStore,
                    configuration: TouchZoneConfiguration
            ) : this(store, configuration, ActionID.NONE)

            var configuration by store.value(configuration)

            var first by store.value(first)
            var middle1 by store.value(middle)
            var middle2 by store.value(middle)
            var last by store.value(last)

            fun verticalProperty(zone: TouchZone): KMutableProperty0<ActionID> = when (zone) {
                TouchZone.TOP_LEFT -> ::first
                TouchZone.TOP_MIDDLE1 -> ::middle1
                TouchZone.TOP_MIDDLE2 -> ::middle2
                TouchZone.TOP_RIGHT -> ::last
                TouchZone.MIDDLE1_LEFT -> ::first
                TouchZone.MIDDLE1_MIDDLE1 -> ::middle1
                TouchZone.MIDDLE1_MIDDLE2 -> ::middle2
                TouchZone.MIDDLE1_RIGHT -> ::last
                TouchZone.MIDDLE2_LEFT -> ::first
                TouchZone.MIDDLE2_MIDDLE1 -> ::middle1
                TouchZone.MIDDLE2_MIDDLE2 -> ::middle2
                TouchZone.MIDDLE2_RIGHT -> ::last
                TouchZone.BOTTOM_LEFT -> ::first
                TouchZone.BOTTOM_MIDDLE1 -> ::middle1
                TouchZone.BOTTOM_MIDDLE2 -> ::middle2
                TouchZone.BOTTOM_RIGHT -> ::last
            }

            fun horizontalProperty(zone: TouchZone): KMutableProperty0<ActionID> = when (zone) {
                TouchZone.TOP_LEFT -> ::first
                TouchZone.TOP_MIDDLE1 -> ::first
                TouchZone.TOP_MIDDLE2 -> ::first
                TouchZone.TOP_RIGHT -> ::first
                TouchZone.MIDDLE1_LEFT -> ::middle1
                TouchZone.MIDDLE1_MIDDLE1 -> ::middle1
                TouchZone.MIDDLE1_MIDDLE2 -> ::middle1
                TouchZone.MIDDLE1_RIGHT -> ::middle1
                TouchZone.MIDDLE2_LEFT -> ::middle2
                TouchZone.MIDDLE2_MIDDLE1 -> ::middle2
                TouchZone.MIDDLE2_MIDDLE2 -> ::middle2
                TouchZone.MIDDLE2_RIGHT -> ::middle2
                TouchZone.BOTTOM_LEFT -> ::last
                TouchZone.BOTTOM_MIDDLE1 -> ::last
                TouchZone.BOTTOM_MIDDLE2 -> ::last
                TouchZone.BOTTOM_RIGHT -> ::last
            }
        }

        class SingleTaps(store: ValueStore) : Taps(
                store,
                TouchZoneConfiguration.NINE,
                left = ActionID.GO_PAGE_PREVIOUS,
                middle = ActionID.SHOW_MENU,
                right = ActionID.GO_PAGE_NEXT
        )

        class LongTaps(store: ValueStore) : Taps(store, TouchZoneConfiguration.SINGLE, ActionID.WORD_SELECT)
        class DoubleTaps(store: ValueStore) : Taps(store, TouchZoneConfiguration.SINGLE, ActionID.NONE)
        class TwoFingersSingleTaps(store: ValueStore) : Taps(store, TouchZoneConfiguration.SINGLE)
        class TwoFingersLongTaps(store: ValueStore) : Taps(store, TouchZoneConfiguration.SINGLE)
        class TwoFingersDoubleTaps(store: ValueStore) : Taps(store, TouchZoneConfiguration.SINGLE)

        class LeftScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.SCROLL_PAGE)
        class RightScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.SCROLL_PAGE)

        class UpScrolls(store: ValueStore) : Scrolls(
                store,
                TouchZoneConfiguration.THREE_COLUMNS_FIXED,
                ActionID.SETTINGS_SCREEN_BRIGHTNESS_CHANGE,
                ActionID.SCROLL_PAGE,
                ActionID.SCROLL_PAGE
        )

        class DownScrolls(store: ValueStore) : Scrolls(
                store,
                TouchZoneConfiguration.THREE_COLUMNS_FIXED,
                ActionID.SETTINGS_SCREEN_BRIGHTNESS_CHANGE,
                ActionID.SCROLL_PAGE,
                ActionID.SCROLL_PAGE
        )

        class TwoFingersLeftScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.GO_PAGE_NEXT_10)
        class TwoFingersRightScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.GO_PAGE_PREVIOUS_10)
        class TwoFingersUpScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.SETTINGS_FORMAT_LINE_HEIGHT_CHANGE)
        class TwoFingersDownScrolls(store: ValueStore) : Scrolls(store, TouchZoneConfiguration.SINGLE, ActionID.SETTINGS_FORMAT_LINE_HEIGHT_CHANGE)

        class TwoFingersPinches(store: ValueStore) {
            var pinchIn by store.value(ActionID.SETTINGS_FONT_SIZE_CHANGE)
            var pinchOut by store.value(ActionID.SETTINGS_FONT_SIZE_CHANGE)
        }
    }

    class HardKeys(store: ValueStore) {
        var volumeDown by store.value(ActionID.GO_PAGE_NEXT_10)
        var volumeUp by store.value(ActionID.GO_PAGE_PREVIOUS_10)

        fun property(hardKey: HardKey): KMutableProperty0<ActionID> = when (hardKey) {
            HardKey.VOLUME_DOWN -> ::volumeDown
            HardKey.VOLUME_UP -> ::volumeUp
        }
    }
}