package com.dmi.util.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router

abstract class BaseActivity protected constructor() : AppCompatActivity() {
    private val ROOT_CONTROLLER_TAG = "____ROOT"

    protected lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootContainer = FrameLayout(this)
        setContentView(rootContainer)
        router = Conductor.attachRouter(this, rootContainer, savedInstanceState);
    }

    protected fun inject(vararg modules: Any) {
        //        val baseApplication = application as BaseApplication
        //        baseApplication.objectGraph().plus(*modules).inject(this)
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> findRootController() = router.getControllerWithTag(ROOT_CONTROLLER_TAG) as T?

    protected fun <T : BaseController> setRootController(controller: T): T {
        router.setRoot(controller, ROOT_CONTROLLER_TAG)
        return controller
    }

    protected inline fun <T : BaseController> initRootController(init: () -> T) =
            findRootController<T>() ?: setRootController(init())
}
