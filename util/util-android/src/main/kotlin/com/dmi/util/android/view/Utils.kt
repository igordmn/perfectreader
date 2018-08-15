package com.dmi.util.android.view

import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.dip

fun View.showHint() {
    val contentDesc = contentDescription.toString()
    if (!TextUtils.isEmpty(contentDesc)) {
        val offset = dip(32f)
        val screenPos = IntArray(2)
        val displayFrame = Rect()
        getLocationOnScreen(screenPos)
        getWindowVisibleDisplayFrame(displayFrame)
        val y = screenPos[1]
        val screenHeight = displayFrame.height()
        val onTopPartOfScreen = y + height / 2 <= screenHeight / 2

        val toast = Toast.makeText(context, contentDesc, Toast.LENGTH_SHORT)
        if (onTopPartOfScreen) {
            toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, y + height + offset)
        } else {
            toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, screenHeight - y + offset)
        }
        toast.show()
    }
}