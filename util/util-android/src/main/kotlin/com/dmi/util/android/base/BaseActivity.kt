package com.dmi.util.android.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dmi.util.android.system.ActivityLifeCycle

abstract class BaseActivity<V : BaseView, VM : BaseViewModel> protected constructor() : AppCompatActivity() {
    val lifeCycle = ActivityLifeCycle()

    protected lateinit var viewModel: VM
    protected lateinit var view: V

    protected abstract fun createViewModel(): VM
    protected abstract fun createView(viewModel: VM): V

    protected fun recreateViewModel() {
        viewModel.destroy()
        viewModel = createViewModel()
        view.destroy()
        view = createView(viewModel)
        setContentView(view.widget)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (lastCustomNonConfigurationInstance != null) {
            viewModel = lastCustomNonConfigurationInstance as VM
        } else {
            viewModel = createViewModel()
        }
        view = createView(viewModel)
        setContentView(view.widget)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations)
            viewModel.destroy()
        view.destroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        lifeCycle.onResume()
    }

    override fun onPause() {
        lifeCycle.onPause()
        super.onPause()
    }

    override fun onRestoreInstanceState(state: Bundle) {
        super.onRestoreInstanceState(state)
        viewModel.restore(state)
    }

    override fun onSaveInstanceState(state: Bundle) {
        viewModel.save(state)
        super.onSaveInstanceState(state)
    }

    final override fun onRetainCustomNonConfigurationInstance() = viewModel
}