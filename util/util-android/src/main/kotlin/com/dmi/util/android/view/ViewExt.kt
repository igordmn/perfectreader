package com.dmi.util.android.view

import android.view.MenuItem
import android.view.View
import com.dmi.util.graphic.Size
import org.jetbrains.anko.onLayoutChange

val View.size: Size get() = Size(width, height)

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

fun MenuItem.onClick(action: () -> Unit) = setOnMenuItemClickListener { action(); true }