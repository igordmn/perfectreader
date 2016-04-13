package com.dmi.util.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.ChildControllerTransaction
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.dmi.util.layout.HasLayout
import com.dmi.util.persist.StateSaver
import com.dmi.util.view.ViewBinder
import dagger.ObjectGraph
import java.io.Serializable

abstract class BaseController : Controller {
    private var savedArgumentIndex = 0
    private var restoredArgumentIndex = 0

    private val layoutId: Int
    private val viewBinder = ViewBinder()
    private val viewStateSaver = StateSaver("__VIEW_STATES")
    private val instanceStateSaver = StateSaver("__INSTANCE_STATES")

    protected open val presenter: BasePresenter? = null

    constructor() : super(Bundle.EMPTY)

    init {
        val hasLayout = javaClass.getAnnotation(HasLayout::class.java)
        this.layoutId = hasLayout.value
    }

    override fun onDestroy() {
        presenter?.onDestroy()
    }

    protected open fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
        return ObjectGraph.create()
    }

    override final fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
        val view = layoutInflater.inflate(layoutId, viewGroup, false)
        viewBinder.bind()
        onViewCreated(view)
        return view
    }

    override final fun onDestroyView(view: View): Unit {
        onViewDestroyed(view)
        viewBinder.unbind()
    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        viewStateSaver.restore(savedViewState)
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        viewStateSaver.save(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        instanceStateSaver.restore(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        instanceStateSaver.save(outState)
    }

    open fun onViewCreated(view: View) = Unit
    open fun onViewDestroyed(view: View) = Unit

    @Suppress("UNCHECKED_CAST")
    protected fun <A : Serializable?> restoreArgument(bundle: Bundle) =
            bundle.getSerializable("ARGUMENT${restoredArgumentIndex++}") as A

    protected fun <A : Serializable?> saveArgument(value: A): A {
        args.putSerializable("ARGUMENT${savedArgumentIndex++}", value)
        return value
    }

    protected fun <V : View> bindView(id: Int) = viewBinder.register<V> { view.findViewById(id) }
    protected fun <V : Serializable?> viewState(initial: V) = viewStateSaver.register(initial)
    protected fun <V : Serializable?> instanceState(initial: V) = viewStateSaver.register(initial)

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
