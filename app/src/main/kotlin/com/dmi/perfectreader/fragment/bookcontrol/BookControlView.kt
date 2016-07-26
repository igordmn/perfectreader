package com.dmi.perfectreader.fragment.bookcontrol

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.fragment.bookcontrol.entity.HardKey
import com.dmi.perfectreader.fragment.bookcontrol.entity.TouchInfo
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.widget.onKey
import com.dmi.util.android.widget.onSizeChange

open class BookControlView(
        context: Context,
        private val model: BookControl
) : BaseView(FrameLayout(context)), View.OnTouchListener {
    init {
        widget.isClickable = false
        widget.isFocusable = false
        widget.setOnTouchListener(this)
        widget.onSizeChange { size, oldSize ->
            model.resize(size.toFloat())
        }
        widget.onKey { keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                onKeyDown(keyCode)
            } else {
                true
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> model.onTouchDown(touchInfo(event))
            MotionEvent.ACTION_MOVE -> model.onTouchMove(touchInfo(event))
            MotionEvent.ACTION_UP -> model.onTouchUp(touchInfo(event))
            else -> Unit
        }
        return true
    }

    private fun onKeyDown(keyCode: Int): Boolean {
        val hardKey = fromKeyCode(keyCode)
        if (hardKey != null)
            model.onKeyDown(hardKey)
        return true
    }

    private fun touchInfo(event: MotionEvent) = TouchInfo(event.x, event.y)

    private fun fromKeyCode(keyCode: Int) = when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_UP -> HardKey.VOLUME_UP
        KeyEvent.KEYCODE_VOLUME_DOWN -> HardKey.VOLUME_DOWN
        KeyEvent.KEYCODE_MENU -> HardKey.MENU
        KeyEvent.KEYCODE_BACK -> HardKey.BACK
        KeyEvent.KEYCODE_SEARCH -> HardKey.SEARCH
        KeyEvent.KEYCODE_CAMERA -> HardKey.CAMERA
        KeyEvent.KEYCODE_DPAD_CENTER -> HardKey.TRACKBALL_PRESS
        KeyEvent.KEYCODE_DPAD_LEFT -> HardKey.TRACKBALL_LEFT
        KeyEvent.KEYCODE_DPAD_RIGHT -> HardKey.TRACKBALL_RIGHT
        KeyEvent.KEYCODE_DPAD_UP -> HardKey.TRACKBALL_UP
        KeyEvent.KEYCODE_DPAD_DOWN -> HardKey.TRACKBALL_DOWN
        else -> null
    }
}