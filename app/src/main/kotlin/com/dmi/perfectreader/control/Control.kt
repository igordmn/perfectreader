package com.dmi.perfectreader.control

import com.dmi.perfectreader.Main
import com.dmi.perfectreader.reader.Reader
import com.dmi.perfectreader.reader.action.ReaderActions
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.action.TouchActionPerformer
import com.dmi.util.graphic.SizeF
import com.dmi.util.input.GestureDetector
import com.dmi.util.input.HardKey
import com.dmi.util.input.TouchEvent
import kotlinx.coroutines.android.UI

class Control(
        private val main: Main,
        private val reader: Reader,
        private val actions: ReaderActions = reader.actions,
        private val settings: Settings = main.settings
) {
    private var gestureDetector: GestureDetector? = null

    fun resize(size: SizeF) {
        gestureDetector?.cancel()
        gestureDetector = gestureDetector(size, main, reader)
    }

    fun onTouchEvent(touchEvent: TouchEvent) = gestureDetector?.onTouchEvent(touchEvent)
    fun cancelTouch() = gestureDetector?.cancel()

    fun onKeyDown(hardKey: HardKey) {
        gestureDetector?.cancel()
        val actionID = settings.control.hardKeys.singlePress.property(hardKey).get()
        actions[actionID].perform()
    }
}

fun gestureDetector(size: SizeF, main: Main, reader: Reader): GestureDetector {
    val settings = main.settings
    val actionProvider = ReaderActionProvider(size, main.density, settings, reader)
    val listener = TouchActionPerformer(actionProvider)
    return GestureDetector(
            UI,
            listener,
            settings.control.touches.doubleTapEnabled,
            settings.control.touches.tapMaxOffset * main.density,
            settings.control.touches.longTapTimeout,
            settings.control.touches.doubleTapTimeout
    )
}