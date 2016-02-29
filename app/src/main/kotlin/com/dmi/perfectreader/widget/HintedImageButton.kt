package com.dmi.perfectreader.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils.isEmpty
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.dmi.util.Units.dipToPx

class HintedImageButton : ImageButton {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnLongClickListener( View.OnLongClickListener { v ->
            showContentDescription()
            true
        })
    }

    override fun setOnLongClickListener(l: View.OnLongClickListener?) {
        throw UnsupportedOperationException()
    }

    private fun showContentDescription() {
        val contentDesc = contentDescription.toString()
        if (!isEmpty(contentDesc)) {
            val OFFSET = dipToPx(32f).toInt()

            val screenPos = IntArray(2)
            val displayFrame = Rect()
            getLocationOnScreen(screenPos)
            getWindowVisibleDisplayFrame(displayFrame)
            val y = screenPos[1]
            val height = height
            val screenHeight = displayFrame.height()
            val onTopPartOfScreen = y + height / 2 <= screenHeight / 2

            val toast = Toast.makeText(context, contentDesc, Toast.LENGTH_SHORT)
            if (onTopPartOfScreen) {
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, y + height + OFFSET)
            } else {
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, screenHeight - y + OFFSET)
            }
            toast.show()
        }
    }
}
