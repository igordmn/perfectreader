package com.dmi.perfectreader.selection

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import com.dmi.perfectreader.R
import com.dmi.util.android.base.*
import com.dmi.util.android.widget.addHintOnLongClick
import com.dmi.util.android.widget.fadeTransition
import com.dmi.util.android.widget.onSizeChange
import com.dmi.util.android.widget.size
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.RectF
import com.dmi.util.graphic.distanceToRect
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch
import java.lang.Math.round

class SelectionView(
        context: Context,
        private val model: Selection
) : BaseView(context, R.layout.fragment_selection) {
    private val HANDLE_WIDTH = round(dip2Px(24F))
    private val ADDITIONAL_TOUCH_RADIUS = round(dip2Px(24F))

    private val handlesContainer = find<FrameLayout>(R.id.handlesContainer)
    private val actionsContainer = find<FrameLayout>(R.id.actionsContainer)
    private val actions = find<View>(R.id.actions)
    private val copyTextButton = find<ImageButton>(R.id.copyTextButton)

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
        handlesContainer.addView(handlesView)

        subscribe(model.leftHandleObservable) {
            updateHandle(leftHandle, it)
            handlesView.invalidate()
            updateActions()
        }
        subscribe(model.rightHandleObservable) {
            updateHandle(rightHandle, it)
            handlesView.invalidate()
            updateActions()
        }
        handlesView.onTouch { view, motionEvent -> onTouch(motionEvent) }

        initActions()

        subscribe(model.onSelectionCopiedToClipboard) {
            toast(string(R.string.selectionCopiedToClipboard))
        }
    }

    private fun initActions() {
        subscribe(model.actionsIsVisibleObservable) {
            updateActions()
        }

        actionsContainer.layoutTransition = fadeTransition(200)
        actionsContainer.onSizeChange { size, oldSize ->
            updateActions()
        }

        actions.onSizeChange { size, oldSize ->
            updateActions()
        }

        DrawableCompat.setTint(copyTextButton.drawable, color(R.color.icon_dark))
        addHintOnLongClick(copyTextButton)

        copyTextButton.onClick {
            model.copySelectedText()
        }
    }

    private fun updateHandle(view: HandleView, model: Selection.Handle) {
        when (model) {
            is Selection.Handle.Invisible -> {
                view.isVisible = false
            }
            is Selection.Handle.Visible -> {
                view.isVisible = true
                view.position = model.bottom
                view.alpha = 255
            }
            is Selection.Handle.NotOnPage -> {
                view.isVisible = true
                view.position = model.bottom
                view.alpha = 127
            }
        }
    }

    private fun updateActions() {
        actions.visibility = if (model.actionsIsVisible) View.VISIBLE else View.GONE

        val position = model.actionsPosition(actionsContainer.size, actions.size)
        val layoutParams = actions.layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin = position.x
        layoutParams.topMargin = position.y
        actions.layoutParams = layoutParams
    }

    private var touchedHandleInfo: TouchedHandleInfo? = null

    private fun onTouch(event: MotionEvent): Boolean {
        val touchRadius = event.touchMajor / 2 + ADDITIONAL_TOUCH_RADIUS
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