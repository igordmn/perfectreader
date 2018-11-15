package com.dmi.util.android.view

import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.dmi.util.graphic.Size
import kotlinx.coroutines.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLayoutChange
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.sdk27.coroutines.onTouch

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

fun View.onContinuousClick(repeatMillis: Long = 200, action: () -> Unit) {
    var job: Job? = null
    onClick {
        action()
    }
    onLongClick(returnValue = true) {
        action()
        job = GlobalScope.launch(Dispatchers.Main) {
            while(true) {
                delay(repeatMillis)
                action()
            }
        }
    }
    onTouch { _, event ->
        if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
            job?.cancel()
        }
    }
}