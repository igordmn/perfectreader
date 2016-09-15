package com.dmi.perfectreader.fragment.control

import com.dmi.perfectreader.fragment.reader.action.ReaderActions
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.input.GestureDetector
import com.dmi.util.input.HardKey
import com.dmi.util.input.TouchEvent
import com.dmi.util.setting.Settings
import com.dmi.perfectreader.data.UserSettingKeys.Control as ControlKeys
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class Control(
        private val settings: Settings,
        private val createGestureDetector: (SizeF) -> GestureDetector,
        private val actions: ReaderActions
) : BaseViewModel() {
    private lateinit var size: SizeF
    private var gestureDetector: GestureDetector? = null

    override fun destroy() {
        super.destroy()
        gestureDetector?.cancel()
    }

    fun resize(size: SizeF) {
        this.size = size
        reload()
    }

    fun reload() {
        gestureDetector?.cancel()
        gestureDetector = createGestureDetector(size)
    }

    fun onTouchEvent(touchEvent: TouchEvent) = gestureDetector?.onTouchEvent(touchEvent)
    fun cancelTouch() = gestureDetector?.cancel()

    fun onKeyDown(hardKey: HardKey) {
        gestureDetector?.cancel()
        val actionID = settings[ControlKeys.HardKeys.SinglePress[hardKey]]
        actions[actionID].perform()
    }
}