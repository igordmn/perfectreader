package com.dmi.perfectreader.control

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.common.ViewContext
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.input.hardKeyFromKeyCode
import com.dmi.util.android.input.performTouchEvents
import com.dmi.util.android.widget.onSizeChange

class ControlView(
        viewContext: ViewContext,
        private val model: Control
) : BaseView(FrameLayout(viewContext.android)), View.OnTouchListener {
    init {
        widget.isClickable = false
        widget.isFocusable = false
        widget.setOnTouchListener(this)
        widget.onSizeChange { size, _ ->
            model.resize(size.toFloat())
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when(event.actionMasked) {
            MotionEvent.ACTION_CANCEL -> model.cancelTouch()
            else -> event.performTouchEvents { model.onTouchEvent(it) }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val hardKey = hardKeyFromKeyCode(keyCode)
        if (hardKey != null)
            model.onKeyDown(hardKey)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }
}