package com.dmi.perfectreader.fragment.control

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.input.hardKeyFromKeyCode
import com.dmi.util.android.input.performTouchEvents
import com.dmi.util.android.widget.onSizeChange

class ControlView(
        context: Context,
        private val model: Control
) : BaseView(FrameLayout(context)), View.OnTouchListener {
    init {
        widget.isClickable = false
        widget.isFocusable = false
        widget.setOnTouchListener(this)
        widget.onSizeChange { size, oldSize ->
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