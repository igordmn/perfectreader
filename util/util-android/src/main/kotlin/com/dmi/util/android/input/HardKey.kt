package com.dmi.util.android.input

import android.view.KeyEvent
import com.dmi.util.input.HardKey

fun hardKeyFromKeyCode(keyCode: Int) = when (keyCode) {
    KeyEvent.KEYCODE_VOLUME_UP -> HardKey.VOLUME_UP
    KeyEvent.KEYCODE_VOLUME_DOWN -> HardKey.VOLUME_DOWN
    else -> null
}