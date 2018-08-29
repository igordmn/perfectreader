package com.dmi.util.android.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dmi.util.scope.Scoped
import com.dmi.util.system.ChangingApplicationWindow

abstract class ActivityExt<M : Scoped> protected constructor() : AppCompatActivity() {
    val window = ChangingApplicationWindow().apply {
        exit = {
            finish()
        }
    }

    protected lateinit var model: M

    protected abstract fun createModel(): M
    protected abstract fun createView(model: M): View

    protected fun recreateModel() {
        model.scope.dispose()
        model = createModel()
        setContentView(createView(model))
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = createModel()
        setContentView(createView(model))
    }

    override fun onDestroy() {
        model.scope.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        window.isActive = true
    }

    override fun onPause() {
        window.isActive = false
        super.onPause()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return getWindow().decorView.interceptKeys(event) || super.dispatchKeyEvent(event)
    }

    private fun View.interceptKeys(event: KeyEvent): Boolean {
        if (this is KeyInterceptable) {
            if (onInterceptKey(event))
                return true
        }
        if (this is ViewGroup) {
            for (i in childCount - 1 downTo 0) {
                if (getChildAt(i).interceptKeys(event))
                    return true
            }
        }
        return false
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val focus = currentFocus
        return if (
                event.action == MotionEvent.ACTION_DOWN &&
                focus != null &&
                focus is ClearFocusOnClickOutside &&
                event !in focus
        ) {
            focus.clearFocus()
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }
}