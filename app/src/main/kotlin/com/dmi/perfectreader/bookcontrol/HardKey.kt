package com.dmi.perfectreader.bookcontrol

import android.view.KeyEvent

// todo добавить кнопок (см. Cool Reader и AlReader)
enum class HardKey {
    UNKNOWN,
    VOLUME_UP,
    VOLUME_DOWN,
    MENU,
    BACK,
    SEARCH,
    CAMERA,
    TRACKBALL_PRESS,
    TRACKBALL_LEFT,
    TRACKBALL_RIGHT,
    TRACKBALL_UP,
    TRACKBALL_DOWN;

    companion object {
        fun fromKeyCode(keyCode: Int): HardKey {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> return VOLUME_UP
                KeyEvent.KEYCODE_VOLUME_DOWN -> return VOLUME_DOWN
                KeyEvent.KEYCODE_MENU -> return MENU
                KeyEvent.KEYCODE_BACK -> return BACK
                KeyEvent.KEYCODE_SEARCH -> return SEARCH
                KeyEvent.KEYCODE_CAMERA -> return CAMERA
                KeyEvent.KEYCODE_DPAD_CENTER -> return TRACKBALL_PRESS
                KeyEvent.KEYCODE_DPAD_LEFT -> return TRACKBALL_LEFT
                KeyEvent.KEYCODE_DPAD_RIGHT -> return TRACKBALL_RIGHT
                KeyEvent.KEYCODE_DPAD_UP -> return TRACKBALL_UP
                KeyEvent.KEYCODE_DPAD_DOWN -> return TRACKBALL_DOWN
                else -> return UNKNOWN
            }
        }
    }
}
