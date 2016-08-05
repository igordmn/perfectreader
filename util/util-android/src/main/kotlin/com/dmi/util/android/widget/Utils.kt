package com.dmi.util.android.widget

import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import android.widget.ImageButton
import android.widget.Toast
import org.jetbrains.anko.dip

fun addHintOnLongClick(button: ImageButton) {
    button.setOnLongClickListener{
        val contentDesc = button.contentDescription.toString()
        if (!TextUtils.isEmpty(contentDesc)) {
            val OFFSET = button.dip(32f)

            val screenPos = IntArray(2)
            val displayFrame = Rect()
            button.getLocationOnScreen(screenPos)
            button.getWindowVisibleDisplayFrame(displayFrame)
            val y = screenPos[1]
            val height = button.height
            val screenHeight = displayFrame.height()
            val onTopPartOfScreen = y + height / 2 <= screenHeight / 2

            val toast = Toast.makeText(button.context, contentDesc, Toast.LENGTH_SHORT)
            if (onTopPartOfScreen) {
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, y + height + OFFSET)
            } else {
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, screenHeight - y + OFFSET)
            }
            toast.show()
        }
        true
    }
}