package com.dmi.perfectreader.ui.control

import android.view.KeyEvent
import android.view.MotionEvent
import com.dmi.util.android.input.hardKeyFromKeyCode
import com.dmi.util.android.input.performTouchEvents
import com.dmi.util.android.view.FrameLayoutExt
import com.dmi.util.android.view.ViewBuild
import com.dmi.util.android.view.onSizeChange
import com.dmi.util.input.MissingTouchEvents
import org.jetbrains.anko.sdk27.coroutines.onTouch

fun ViewBuild.controlView(model: Control) = FrameLayoutExt {
    val missingTouchEvents = MissingTouchEvents()

    onTouch(returnValue = true) { _, event ->
        when(event.actionMasked) {
            MotionEvent.ACTION_CANCEL -> model.cancelTouch()
            else -> event.performTouchEvents { original ->
                missingTouchEvents.onTouch(original) { missing ->
                    model.onTouchEvent(missing)
                }
            }
        }
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