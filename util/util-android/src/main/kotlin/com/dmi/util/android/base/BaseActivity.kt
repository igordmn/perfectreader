package com.dmi.util.android.base

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.dmi.util.scope.Scoped
import com.dmi.util.system.ChangingApplicationWindow

abstract class BaseActivity<M : Scoped, V : BaseView> protected constructor() : AppCompatActivity() {
    lateinit var window: ChangingApplicationWindow

    protected lateinit var model: M
    protected lateinit var view: V

    protected abstract fun createModel(): M
    protected abstract fun createView(viewModel: M): V

    protected fun recreateModel() {
        model.scope.dispose()
        model = createModel()
        view.dispose()
        view = createView(model)
        setContentView(view.widget)
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
        setContentView(view.widget)
    }

    override fun onDestroy() {
        window.exit = null
        if (!isChangingConfigurations)
            model.scope.dispose()
        view.dispose()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent) = view.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    override fun onKeyUp(keyCode: Int, event: KeyEvent) = view.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)

    class Retain<VM>(val window: ChangingApplicationWindow, val model: VM)
}