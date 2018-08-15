package com.dmi.perfectreader.control

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import com.dmi.util.android.input.hardKeyFromKeyCode
import com.dmi.util.android.input.performTouchEvents
import com.dmi.util.android.view.FrameLayoutExt
import com.dmi.util.android.view.onSizeChange
import com.dmi.util.android.view.view
import org.jetbrains.anko.onTouch

fun Context.controlView(model: Control) = view(::FrameLayoutExt) {
    onTouch { _, event ->
        when(event.actionMasked) {
            MotionEvent.ACTION_CANCEL -> model.cancelTouch()
            else -> event.performTouchEvents { model.onTouchEvent(it) }
        }
        true
    }

    onSizeChange { size, _ ->
        model.resize(size.toFloat())
    }

    onInterceptKey { event ->
        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                val hardKey = hardKeyFromKeyCode(event.keyCode)
                if (hardKey != null)
                    model.onKeyDown(hardKey)
                true
            }
            KeyEvent.ACTION_UP -> true
            else -> false
        }
    }
}