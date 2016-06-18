package com.dmi.util.widget

import android.view.KeyEvent
import android.view.View
import com.dmi.util.graphic.Size
import org.jetbrains.anko.onLayoutChange

fun View.onSizeChange(listener: (size: Size, oldSize: Size) -> Unit) =
        onLayoutChange { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val width = right - left
            val height = bottom - top
            val oldWidth = oldRight - oldLeft
            val oldHeight = oldBottom - oldTop
            if (oldWidth != width || oldHeight != height) {
                listener(Size(width, height), Size(oldWidth, oldHeight))
            }
        }


fun View.onKey(listener: (keyCode: Int, keyEvent: KeyEvent) -> Boolean) {
    setOnKeyListener { view, keyCode, keyEvent ->
        listener(keyCode, keyEvent)
    }
}

fun View.onKeyDown(listener: (keyCode: Int, keyEvent: KeyEvent) -> Boolean) {
    onKey { keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
            listener(keyCode, keyEvent)
        } else {
            false
        }
    }
}