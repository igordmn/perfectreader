package com.dmi.perfectreader.ui.selection

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import com.dmi.perfectreader.R
import com.dmi.perfectreader.ui.book.selection.BookSelections
import com.dmi.util.android.view.color
import com.dmi.util.android.view.drawable
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.RectF
import com.dmi.util.graphic.distanceToRect
import org.jetbrains.anko.dip

private class HandleView(private val drawable: Drawable) {
    var position: PositionF = PositionF(0F, 0F)
        set(value) {
            field = value
            bounds = drawableBounds + value
        }

    var isVisible: Boolean = false

    private val drawableBounds = RectF(
            drawable.bounds.left.toFloat(),
            drawable.bounds.top.toFloat(),
            drawable.bounds.right.toFloat(),
            drawable.bounds.bottom.toFloat()
    )

    var alpha: Int
        get() = drawable.alpha
        set(value) {
            drawable.alpha = value
        }

    fun draw(canvas: Canvas) {
        if (isVisible) {
            canvas.save()
            canvas.translate(position.x, position.y)
            drawable.draw(canvas)
            canvas.restore()
        }
    }

    var bounds = drawableBounds + position
        private set
}

class HandlesView(context: Context, private val model: Selection) : View(context) {
    private class TouchedHandleInfo(var isLeft: Boolean, val touchOffset: PositionF)

    private val handleWidth = dip(24)
    private val additionalTouchRadius = dip(8)
    private var touchedHandleInfo: TouchedHandleInfo? = null

    private val leftHandle = HandleView(
            drawable(R.drawable.selection_handle_left, color(R.color.secondary)).apply {
                setBounds(-handleWidth, 0, 0, handleWidth)
            }
    )

    private val rightHandle = HandleView(
            drawable(R.drawable.selection_handle_right, color(R.color.secondary)).apply {
                setBounds(0, 0, handleWidth, handleWidth)
            }
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        leftHandle.draw(canvas)
        rightHandle.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchRadius = event.touchMajor / 2 + additionalTouchRadius
        val touchPosition = PositionF(event.x, event.y)

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onTouchDown(touchPosition, touchRadius)
            MotionEvent.ACTION_MOVE -> onTouchMove(touchPosition)
            MotionEvent.ACTION_UP -> onTouchUp()
            else -> false
        }
    }

    private fun onTouchDown(position: PositionF, radius: Float): Boolean {
        val distanceToLeft = if (leftHandle.isVisible) distanceToRect(position, leftHandle.bounds) else Float.MAX_VALUE
        val distanceToRight = if (rightHandle.isVisible) distanceToRect(position, rightHandle.bounds) else Float.MAX_VALUE
        val leftIsNearer = distanceToLeft < distanceToRight

        touchedHandleInfo = if (distanceToLeft <= radius && leftIsNearer) {
            TouchedHandleInfo(true, position - leftHandle.position)
        } else if (distanceToRight <= radius && !leftIsNearer) {
            TouchedHandleInfo(false, position - rightHandle.position)
        } else {
            null
        }

        if (touchedHandleInfo != null)
            model.isSelecting = true

        return touchedHandleInfo != null
    }

    private fun onTouchUp(): Boolean {
        if (touchedHandleInfo != null)
            model.isSelecting = false

        return touchedHandleInfo != null
    }

    private fun onTouchMove(position: PositionF): Boolean {
        val touchedHandleInfo = touchedHandleInfo
        if (touchedHandleInfo != null) {
            val newHandlePosition = position - touchedHandleInfo.touchOffset
            touchedHandleInfo.isLeft = model.moveHandleTo(newHandlePosition, touchedHandleInfo.isLeft)
        }
        return touchedHandleInfo != null
    }

    fun set(handles: BookSelections.Handles) {
        set(leftHandle, handles.left)
        set(rightHandle, handles.right)
        invalidate()
    }

    private fun set(view: HandleView, model: BookSelections.Handle) {
        when (model) {
            is BookSelections.Handle.Invisible -> {
                view.isVisible = false
            }
            is BookSelections.Handle.Visible -> {
                view.isVisible = true
                view.position = model.bottom
                view.alpha = 255
            }
            is BookSelections.Handle.NotOnPage -> {
                view.isVisible = true
                view.position = model.bottom
                view.alpha = 127
            }
        }
    }
}