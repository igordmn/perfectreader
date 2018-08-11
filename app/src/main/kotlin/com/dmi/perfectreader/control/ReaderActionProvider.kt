package com.dmi.perfectreader.control

import com.dmi.perfectreader.data.UserSettingKeys.Control.Touches
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.reader.Reader
import com.dmi.util.action.Action
import com.dmi.util.action.TouchActionPerformer
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.action.performAction
import com.dmi.util.graphic.SizeF
import com.dmi.util.input.TouchArea

class ReaderActionProvider(
        private val size: SizeF,
        private val density: Float,
        private val settings: UserSettings,
        private val reader: Reader
) : TouchActionPerformer.Provider {
    private val CANCEL_SELECTION_ACTION = performAction { reader.book.cancelSelection() }
    private val actions = reader.actions

    override fun singleTap(area: TouchArea): Action {
        if (reader.book.isSelected) {
            return CANCEL_SELECTION_ACTION
        } else {
            return tapAction(area, Touches.SingleTaps)
        }
    }

    override fun longTap(area: TouchArea) = tapAction(area, Touches.LongTaps)
    override fun doubleTap(area: TouchArea) = tapAction(area, Touches.DoubleTaps)
    override fun twoFingersSingleTap(area: TouchArea) = tapAction(area, Touches.TwoFingersSingleTaps)
    override fun twoFingersLongTap(area: TouchArea) = tapAction(area, Touches.TwoFingersLongTaps)
    override fun twoFingersDoubleTap(area: TouchArea) = tapAction(area, Touches.TwoFingersDoubleTaps)
    override fun scrollLeft(area: TouchArea) = horizontalScrollAction(area, Touches.LeftScrolls)
    override fun scrollRight(area: TouchArea) = horizontalScrollAction(area, Touches.RightScrolls)
    override fun scrollUp(area: TouchArea) = verticalScrollAction(area, Touches.UpScrolls)
    override fun scrollDown(area: TouchArea) = verticalScrollAction(area, Touches.DownScrolls)
    override fun twoFingersScrollLeft(area: TouchArea) = horizontalScrollAction(area, Touches.TwoFingersLeftScrolls)
    override fun twoFingersScrollRight(area: TouchArea) = horizontalScrollAction(area, Touches.TwoFingersRightScrolls)
    override fun twoFingersScrollUp(area: TouchArea) = verticalScrollAction(area, Touches.TwoFingersUpScrolls)
    override fun twoFingersScrollDown(area: TouchArea) = verticalScrollAction(area, Touches.TwoFingersDownScrolls)

    override fun twoFingersPinchIn(): Action {
        val actionID = settings[Touches.TwoFingersPinches.pinchIn]
        return actions[actionID]
    }

    override fun twoFingersPinchOut(): Action {
        val actionID = settings[Touches.TwoFingersPinches.pinchOut]
        return actions[actionID]
    }

    private fun tapAction(area: TouchArea, tapKeys: Touches.Taps): Action {
        val configuration = settings[tapKeys.configuration]
        val zone = configuration[area]
        val actionID = settings[tapKeys[zone]]
        return actions[actionID]
    }

    private fun verticalScrollAction(area: TouchArea, scrollKeys: Touches.Scrolls): Action {
        val configuration = settings[scrollKeys.configuration]
        val zone = configuration[area]
        val actionID = settings[scrollKeys.getVertical(zone)]
        return actions[actionID]
    }

    private fun horizontalScrollAction(area: TouchArea, scrollKeys: Touches.Scrolls): Action {
        val configuration = settings[scrollKeys.configuration]
        val zone = configuration[area]
        val actionID = settings[scrollKeys.getHorizontal(zone)]
        return actions[actionID]
    }

    private operator fun TouchZoneConfiguration.get(area: TouchArea) = getAt(area.x / density, area.y / density, size.width / density, size.height / density)
}