package com.dmi.util.android.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dmi.util.scope.Disposable

abstract class ActivityExt<M : Disposable> protected constructor() : AppCompatActivity() {
    protected lateinit var model: M

    protected abstract fun createModel(stateData: ByteArray?): M
    protected abstract fun saveModel(model: M): ByteArray
    protected abstract fun view(model: M): View

    protected fun recreateModel() {
        model.dispose()
        model = createModel(null)
        setContentView(view(model).restorable())
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = createModel(savedInstanceState?.getByteArray("stateData"))
        setContentView(view(model).restorable())
    }

    override fun onDestroy() {
        model.dispose()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putByteArray("stateData", saveModel(model))
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return window.decorView.interceptKeys(event) || super.dispatchKeyEvent(event)
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