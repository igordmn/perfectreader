package com.dmi.util.android.base

import android.content.Context
import android.support.annotation.CallSuper
import android.view.View
import android.view.ViewGroup
import com.dmi.util.android.ext.dipToPx
import com.dmi.util.android.ext.dipToPx
import com.dmi.util.android.ext.inflate
import com.dmi.util.android.ext.px2dip
import com.dmi.util.android.ext.px2dip
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.returnValue
import com.dmi.util.refWatcher
import org.jetbrains.anko.find
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.*

abstract class BaseView(val widget: ViewGroup) {
    constructor(context: Context, layoutId: Int) : this(context.inflate(layoutId))

    private val subscriptions = CompositeSubscription()
    private val children = LinkedHashSet<BaseView>()
    private val childToContainer = HashMap<BaseView, ViewGroup>()

    @CallSuper
    open fun destroy() {
        children.forEach { it.destroy() }
        subscriptions.clear()
        refWatcher.watch(this)
    }

    protected fun <T : BaseView> addChild(child: T, containerId: Int) = addChild(child, find<ViewGroup>(containerId))

    protected fun <T : BaseView> addChild(child: T, container: ViewGroup): T {
        require(container.childCount == 0)
        children.add(child)
        childToContainer[child] = container
        container.addView(child.widget)
        return child
    }

    protected fun <T : BaseView> removeChild(child: T) {
        child.destroy()
        children.remove(child)
        childToContainer[child]!!.removeView(child.widget)
    }

    protected fun <V : BaseView, VM : BaseViewModel> toggleChildByModel(model: VM?, current: V?, containerId: Int, create: (VM) -> V) =
            toggleChildByModel(model, current, find<ViewGroup>(containerId), create)

    protected fun <V : BaseView, VM : BaseViewModel> toggleChildByModel(model: VM?, current: V?, container: ViewGroup, create: (VM) -> V) =
            if (model != null && current == null) {
                addChild(create(model), container)
            } else if (model == null && current != null) {
                removeChild(current).returnValue(null)
            } else {
                current
            }

    protected fun <T> subscribe(observable: Observable<T>, onNext: (T) -> Unit) =
            subscriptions.add(observable.subscribe(onNext))
}

inline fun <reified T : View> BaseView.find(id: Int): T = widget.find(id)
fun BaseView.dipToPx(value: Float) = widget.dipToPx(value)
fun BaseView.dipToPx(size: SizeF) = widget.dipToPx(size)
fun BaseView.px2dip(px: Float) = widget.px2dip(px)
fun BaseView.px2dip(size: SizeF) = widget.px2dip(size)