package com.dmi.util.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.bluelinelabs.conductor.ChildControllerTransaction
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.dmi.util.layout.HasLayout
import dagger.ObjectGraph
import icepick.Icepick

abstract class BaseController : Controller {
    private val layoutId: Int

    protected open val presenter: BasePresenter? = null

    constructor() : super()

    constructor(bundle: Bundle) : super(bundle)

    init {
        val hasLayout = javaClass.getAnnotation(HasLayout::class.java)
        this.layoutId = hasLayout.value
    }

    override fun onDestroy() {
        presenter?.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Icepick.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    protected open fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return ObjectGraph.create()
    }

    override final fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
        val view = layoutInflater.inflate(layoutId, viewGroup, false)
        ButterKnife.bind(this, view)
        onViewCreated(view)
        return view
    }

    override final fun onDestroyView(view: View): Unit {
        onViewDestroyed(view)
        ButterKnife.unbind(this)
    }

    open fun onViewCreated(view: View) = Unit
    open fun onViewDestroyed(view: View) = Unit

    protected inline fun <reified T : BaseController> addChild(controller: T, containerId: Int, tag: String? = T::class.java.name): T {
        addChildController(ChildControllerTransaction
                .builder(controller, containerId)
                .tag(tag)
                .build()
        )
        return controller
    }

    protected inline fun <reified T : BaseController> addChild(
            controller: T,
            changeHandler: ControllerChangeHandler,
            containerId: Int,
            tag: String? = T::class.java.name
    ): T {
        addChildController(ChildControllerTransaction
                .builder(controller, containerId)
                .tag(tag)
                .pushChangeHandler(changeHandler)
                .popChangeHandler(changeHandler)
                .build()
        )
        return controller
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T : BaseController> initChild(
            containerId: Int,
            tag: String? = T::class.java.name,
            init: () -> T
    ) = getChildController(tag) as T? ?: addChild(init(), containerId, tag)

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T : Any> findChild(tag: String? = T::class.java.name) = getChildController(tag) as T?
}
