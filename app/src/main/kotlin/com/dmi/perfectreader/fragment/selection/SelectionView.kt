package com.dmi.perfectreader.fragment.selection

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.R
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.base.color
import com.dmi.util.android.base.dip2Px
import com.dmi.util.android.base.drawable
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.RectF
import com.dmi.util.graphic.distanceToRect
import org.jetbrains.anko.onTouch
import java.lang.Math.round

class SelectionView(
        context: Context,
        private val model: Selection
) : BaseView(FrameLayout(context)) {
    private val HANDLE_WIDTH = round(dip2Px(24F))
    private val ADDITIONAL_TOUCH_RADIUS = round(dip2Px(24F))

    private val leftHandle = HandleView(
            drawable(R.drawable.selection_handle_left, color(R.color.primary)).apply {
                setBounds(-HANDLE_WIDTH, 0, 0, HANDLE_WIDTH)
            }
    )

    private val rightHandle = HandleView(
            drawable(R.drawable.selection_handle_right, color(R.color.primary)).apply {
                setBounds(0, 0, HANDLE_WIDTH, HANDLE_WIDTH)
            }
    )

    init {
        val handlesView = object : View(context) {
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                leftHandle.draw(canvas)
                rightHandle.draw(canvas)
            }
        }
        widget.addView(handlesView)

        subscribe(model.leftHandleObservable) {
            updateHandle(leftHandle, it)
            handlesView.invalidate()
        }
        subscribe(model.rightHandleObservable) {
            updateHandle(rightHandle, it)
            handlesView.invalidate()
        }
        widget.onTouch { view, motionEvent -> onTouch(motionEvent) }
    }

    private fun updateHandle(view: HandleView, model: Selection.Handle) {
        when (model) {
            is Selection.Handle.Invisible -> {
                view.isVisible = false
            }
            is Selection.Handle.Visible -> {
                view.isVisible = true
                view.position = model.position
                view.alpha = 255
            }
            is Selection.Handle.NotOnPage -> {
                view.isVisible = true
                view.position = model.position
                view.alpha = 127
            }
        }
    }

    private var touchedHandleInfo: TouchedHandleInfo? = null

    private fun onTouch(event: MotionEvent): Boolean {
        val touchRadius = event.touchMajor / 2 + ADDITIONAL_TOUCH_RADIUS
        val touchPosition = PositionF(event.x, event.y)

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown(touchPosition, touchRadius)
            MotionEvent.ACTION_MOVE -> onTouchMove(touchPosition)
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

    private class TouchedHandleInfo(var isLeft: Boolean, val touchOffset: PositionF)

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
}