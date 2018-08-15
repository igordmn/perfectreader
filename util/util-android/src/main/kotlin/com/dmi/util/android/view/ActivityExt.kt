package com.dmi.util.android.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dmi.util.scope.Scoped
import com.dmi.util.system.ChangingApplicationWindow

abstract class ActivityExt<M : Scoped> protected constructor() : AppCompatActivity() {
    lateinit var window: ChangingApplicationWindow

    protected lateinit var model: M
    protected lateinit var view: View

    protected abstract fun createModel(): M
    protected abstract fun createView(model: M): View

    protected fun recreateModel() {
        model.scope.dispose()
        model = createModel()
        view = createView(model)
        setContentView(view)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (lastCustomNonConfigurationInstance != null) {
            val retain = lastCustomNonConfigurationInstance as Retain<M>
            this.window = retain.window
            this.model = retain.model
        } else {
            window = ChangingApplicationWindow()
            model = createModel()
        }
        window.exit = {
            finish()
        }
        view = createView(model)
        setContentView(view)
    }

    override fun onDestroy() {
        window.exit = null
        if (!isChangingConfigurations)
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

    final override fun onRetainCustomNonConfigurationInstance() = Retain(window, model)

    class Retain<VM>(val window: ChangingApplicationWindow, val model: VM)

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
}