package com.dmi.perfectreader.control

import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.settings.ControlSettings
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.action.Action
import com.dmi.util.action.TouchActionPerformer
import com.dmi.util.action.TouchZoneConfiguration
import com.dmi.util.action.performAction
import com.dmi.util.graphic.SizeF
import com.dmi.util.input.TouchArea

class ReaderActionProvider(
        private val size: SizeF,
        private val density: Float,
        private val settings: Settings,
        private val reader: Reader
) : TouchActionPerformer.Provider {
    private val cancelSelectionAction = performAction {
        reader.deselect()
    }
    private val actions = reader.actions

    override fun singleTap(area: TouchArea): Action {
        return if (reader.selection != null) {
            cancelSelectionAction
        } else {
            tapAction(area, settings.control.touches.singleTaps)
        }
    }

    override fun longTap(area: TouchArea) = tapAction(area, settings.control.touches.longTaps)
    override fun doubleTap(area: TouchArea) = tapAction(area, settings.control.touches.doubleTaps)
    override fun twoFingersSingleTap(area: TouchArea) = tapAction(area, settings.control.touches.twoFingersSingleTaps)
    override fun twoFingersLongTap(area: TouchArea) = tapAction(area, settings.control.touches.twoFingersLongTaps)
    override fun twoFingersDoubleTap(area: TouchArea) = tapAction(area, settings.control.touches.twoFingersDoubleTaps)
    override fun scrollLeft(area: TouchArea) = horizontalScrollAction(area, settings.control.touches.leftScrolls)
    override fun scrollRight(area: TouchArea) = horizontalScrollAction(area, settings.control.touches.rightScrolls)
    override fun scrollUp(area: TouchArea) = verticalScrollAction(area, settings.control.touches.upScrolls)
    override fun scrollDown(area: TouchArea) = verticalScrollAction(area, settings.control.touches.downScrolls)
    override fun twoFingersScrollLeft(area: TouchArea) = horizontalScrollAction(area, settings.control.touches.twoFingersLeftScrolls)
    override fun twoFingersScrollRight(area: TouchArea) = horizontalScrollAction(area, settings.control.touches.twoFingersRightScrolls)
    override fun twoFingersScrollUp(area: TouchArea) = verticalScrollAction(area, settings.control.touches.twoFingersUpScrolls)
    override fun twoFingersScrollDown(area: TouchArea) = verticalScrollAction(area, settings.control.touches.twoFingersDownScrolls)

    override fun twoFingersPinchIn(): Action {
        val actionID = settings.control.touches.twoFingersPinches.pinchIn
        return actions[actionID]
    }

    override fun twoFingersPinchOut(): Action {
        val actionID = settings.control.touches.twoFingersPinches.pinchOut
        return actions[actionID]
    }

    private fun tapAction(area: TouchArea, tapKeys: ControlSettings.Touches.Taps): Action {
        val configuration = tapKeys.configuration
        val zone = configuration[area]
        val actionID = tapKeys.property(zone).get()
        return actions[actionID]
    }

    private fun verticalScrollAction(area: TouchArea, scrollKeys: ControlSettings.Touches.Scrolls): Action {
        val configuration = scrollKeys.configuration
        val zone = configuration[area]
        val actionID = scrollKeys.verticalProperty(zone).get()
        return actions[actionID]
    }

    private fun horizontalScrollAction(area: TouchArea, scrollKeys: ControlSettings.Touches.Scrolls): Action {
        val configuration = scrollKeys.configuration
        val zone = configuration[area]
        val actionID = scrollKeys.horizontalProperty(zone).get()
        return actions[actionID]
    }

    private operator fun TouchZoneConfiguration.get(area: TouchArea) = getAt(area.x / density, area.y / density, size.width / density, size.height / density)
}