package com.dmi.util.android.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.dmi.util.android.R

class CheckableImageButton(context: Context) : AppCompatImageButton(context), Checkable {
    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    private var checked: Boolean = false
    private var onChecked: ((value: Boolean) -> Unit)? = null

    fun onChecked(action: (value: Boolean) -> Unit) {
        onChecked = action
    }

    init {
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityEvent(host: View, event: AccessibilityEvent) {
                super.onInitializeAccessibilityEvent(host, event)
                event.isChecked = checked
            }

            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.isCheckable = true
                info.isChecked = checked
            }
        })

        background = drawable(R.drawable.checkable_image_button_background)
        updateAlpha()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateAlpha()
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            updateAlpha()
            refreshDrawableState()
            sendAccessibilityEvent(TYPE_WINDOW_CONTENT_CHANGED)
            onChecked?.invoke(checked)
        }
    }

    private fun updateAlpha() {
        drawable?.alpha = if (checked) 255 else (0.60 * 255).toInt()
    }

    override fun isChecked() = checked

    override fun toggle() {
        isChecked = !checked
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (checked)
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        return drawableState
    }
}
