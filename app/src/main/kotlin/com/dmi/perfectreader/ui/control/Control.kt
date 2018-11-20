package com.dmi.perfectreader.ui.control

import com.dmi.perfectreader.settings.Settings
import com.dmi.perfectreader.ui.action.ActionID
import com.dmi.perfectreader.ui.action.Actions
import com.dmi.perfectreader.ui.reader.Reader
import com.dmi.perfectreader.ui.reader.ReaderContext
import com.dmi.util.action.TouchActionPerformer
import com.dmi.util.graphic.SizeF
import com.dmi.util.input.GestureDetector
import com.dmi.util.input.HardKey
import com.dmi.util.input.TouchEvent
import com.dmi.util.scope.Disposable
import kotlinx.coroutines.Dispatchers

class Control(
        private val context: ReaderContext,
        private val reader: Reader,
        private val actions: Actions = reader.actions,
        private val settings: Settings = context.main.settings
): Disposable {
    private var gestureDetector: GestureDetector? = null

    fun resize(size: SizeF) {
        gestureDetector?.cancel()
        gestureDetector = gestureDetector(size, context, reader)
    }

    override fun dispose() {
        gestureDetector?.cancel()
    }

    fun onTouchEvent(touchEvent: TouchEvent) = gestureDetector?.onTouchEvent(touchEvent)
    fun cancelTouch() = gestureDetector?.cancel()

    fun onKeyDown(hardKey: HardKey): Boolean {
        gestureDetector?.cancel()
        val actionID = settings.control.hardKeys.property(hardKey).get()
        return if (actionID != ActionID.NONE) {
            actions[actionID].perform()
            true
        } else {
            false
        }
    }

    fun onKeyUp(hardKey: HardKey): Boolean {
        val actionID = settings.control.hardKeys.property(hardKey).get()
        return actionID != ActionID.NONE
    }
}

fun gestureDetector(size: SizeF, context: ReaderContext, reader: Reader): GestureDetector {
    val settings = context.main.settings
    val actionProvider = ReaderActionProvider(size, context.main.density, settings, reader)
    val listener = TouchActionPerformer(actionProvider)
    return GestureDetector(
            Dispatchers.Main,
            listener,
            settings.control.touches.doubleTapEnabled,
            settings.control.touches.tapMaxOffset * context.main.density,
            settings.control.touches.longTapTimeout,
            settings.control.touches.doubleTapTimeout
    )
}