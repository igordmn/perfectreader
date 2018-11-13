package com.dmi.perfectreader.ui.control

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import com.dmi.util.android.input.hardKeyFromKeyCode
import com.dmi.util.android.input.performTouchEvents
import com.dmi.util.android.view.FrameLayoutExt
import com.dmi.util.android.view.onSizeChange
import org.jetbrains.anko.onTouch

fun controlView(context: Context, model: Control) = FrameLayoutExt(context).apply {
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
        val hardKey = hardKeyFromKeyCode(event.keyCode)
        when {
            hardKey == null -> false
            event.action == KeyEvent.ACTION_DOWN -> model.onKeyDown(hardKey)
            event.action == KeyEvent.ACTION_UP -> model.onKeyUp(hardKey)
            else -> false
        }
    }
}