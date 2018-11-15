package com.dmi.util.android.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.PopupMenu
import org.jetbrains.anko.backgroundColor

/**
 * Warning: don't call setOnDismissListener after using this function
 */
fun PopupMenu(context: Context, container: FrameLayout, x: Float, y: Float): PopupMenu {
    val anchor = View(context)
    anchor.layoutParams = ViewGroup.LayoutParams(1, 1)
    anchor.backgroundColor = Color.TRANSPARENT
    anchor.x = x
    anchor.y = y
    container.addView(anchor)

    val popupMenu = PopupMenu(context, anchor)
//    popupMenu.setOnDismissListener { container.removeView(anchor) }
    return popupMenu
}