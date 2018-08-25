package com.dmi.util.android.view

import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.dmi.util.graphic.Size
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLayoutChange
import org.jetbrains.anko.onLongClick
import org.jetbrains.anko.onTouch

fun View.onSizeChange(listener: (size: Size, oldSize: Size) -> Unit) {
    onLayoutChange { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val width = right - left
        val height = bottom - top
        val oldWidth = oldRight - oldLeft
        val oldHeight = oldBottom - oldTop
        if (oldWidth != width || oldHeight != height) {
            listener(Size(width, height), Size(oldWidth, oldHeight))
        }
    }
}

fun MenuItem.onClick(action: () -> Unit): MenuItem = setOnMenuItemClickListener { action(); true }

fun View.dontSendTouchToParent() = onTouch { _, _ -> true }

fun View.onContinousClick(repeatMillis: Long = 200, action: () -> Unit) {
    var job: Job? = null
    onClick {
        action()
    }
    onLongClick {
        action()
        job = launch(UI) {
            while(true) {
                delay(repeatMillis)
                action()
            }
        }
        true
    }
    onTouch { _, event ->
        if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
            job?.cancel()
            false
        } else {
            false
        }
    }
}