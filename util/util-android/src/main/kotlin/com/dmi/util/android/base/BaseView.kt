package com.dmi.util.android.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.CallSuper
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.dmi.util.android.ext.dip2Px
import com.dmi.util.android.ext.inflate
import com.dmi.util.android.ext.px2dip
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.returnValue
import com.dmi.util.refWatcher
import org.jetbrains.anko.collections.forEachReversed
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.*

abstract class BaseView(val widget: ViewGroup) {
    constructor(context: Context, layoutId: Int) : this(context.inflate(layoutId))

    protected val context: Context = widget.context

    private val subscriptions = CompositeSubscription()
    private val children = LinkedList<BaseView>()
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

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean = childrenHandleKey {
        it.onKeyDown(keyCode, event)
    }

    open fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean = childrenHandleKey {
        it.onKeyUp(keyCode, event)
    }

    open fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean = childrenHandleKey {
        it.onKeyLongPress(keyCode, event)
    }

    open fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean = childrenHandleKey {
        it.onKeyMultiple(keyCode, repeatCount, event)
    }

    private inline fun childrenHandleKey(action: (child: BaseView) -> Boolean): Boolean {
        children.forEachReversed {
            if (action(it))
                return true
        }
        return false
    }
}

inline fun <reified T : View> BaseView.find(id: Int): T = widget.find(id)

fun BaseView.dip2Px(value: Float) = widget.dip2Px(value)
fun BaseView.dip2Px(size: SizeF) = widget.dip2Px(size)
fun BaseView.px2dip(px: Float) = widget.px2dip(px)
fun BaseView.px2dip(size: SizeF) = widget.px2dip(size)

fun BaseView.color(resID: Int) = getColor(widget.context, resID)

fun BaseView.drawable(resID: Int): Drawable = DrawableCompat.wrap(VectorDrawableCompat.create(widget.context.resources, resID, widget.context.theme)!!)
fun BaseView.drawable(resID: Int, tintColor: Int): Drawable = drawable(resID).apply {
    DrawableCompat.setTint(this, tintColor)
}
fun BaseView.string(resID: Int): String = widget.context.getString(resID)
fun BaseView.toast(text: String) = widget.context.toast(text)
fun BaseView.longToast(text: String) = widget.context.longToast(text)