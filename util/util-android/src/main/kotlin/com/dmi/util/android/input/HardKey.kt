package com.dmi.util.android.input

import android.view.KeyEvent
import com.dmi.util.input.HardKey

fun hardKeyFromKeyCode(keyCode: Int) = when (keyCode) {
    KeyEvent.KEYCODE_VOLUME_UP -> HardKey.VOLUME_UP
    KeyEvent.KEYCODE_VOLUME_DOWN -> HardKey.VOLUME_DOWN
    KeyEvent.KEYCODE_MENU -> HardKey.MENU
    KeyEvent.KEYCODE_BACK -> HardKey.BACK
    KeyEvent.KEYCODE_SEARCH -> HardKey.SEARCH
    KeyEvent.KEYCODE_CAMERA -> HardKey.CAMERA
    KeyEvent.KEYCODE_DPAD_CENTER -> HardKey.DPAD_CENTER
    KeyEvent.KEYCODE_DPAD_LEFT -> HardKey.DPAD_LEFT
    KeyEvent.KEYCODE_DPAD_RIGHT -> HardKey.DPAD_RIGHT
    KeyEvent.KEYCODE_DPAD_UP -> HardKey.DPAD_UP
    KeyEvent.KEYCODE_DPAD_DOWN -> HardKey.DPAD_DOWN
    else -> null
}