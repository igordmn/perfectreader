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
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Disposables
import com.dmi.util.scope.Event
import com.dmi.util.scope.Scope.Companion.onchange
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.util.*

abstract class BaseView(val widget: ViewGroup) : Disposable {
    constructor(context: Context, layoutId: Int) : this(context.inflate(layoutId))

    protected val context: Context = widget.context
    private val disposables = Disposables()

    private val children = LinkedList<BaseView>()
    private val childToContainer = HashMap<BaseView, ViewGroup>()

    @CallSuper
    override fun dispose() {
        children.forEach { it.dispose() }
        disposables.dispose()
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
        child.dispose()
        children.remove(child)
        childToContainer[child]!!.removeView(child.widget)
    }

    protected fun <V : BaseView, VM> toggleChildByModel(model: VM?, current: V?, containerId: Int, create: (VM) -> V) =
            toggleChildByModel(model, current, find<ViewGroup>(containerId), create)

    protected fun <V : BaseView, VM> toggleChildByModel(model: VM?, current: V?, container: ViewGroup, create: (VM) -> V) =
            if (model != null && current == null) {
                addChild(create(model), container)
            } else if (model == null && current != null) {
                removeChild(current).returnValue(null)
            } else {
                current
            }

    protected fun autorun(action: () -> Unit) {
        disposables += object : Disposable {
            var subscription: Disposable? = null
            var job: Job? = null

            init {
                deffer()
            }

            fun deffer() {
                if (job == null) {
                    job = launch(UI) {
                        perform()
                        job = null
                    }
                }
            }

            fun perform() {
                subscription?.dispose()
                subscription = onchange(action).subscribe { deffer() }
            }

            override fun dispose() {
                job?.cancel()
                subscription?.dispose()
            }
        }
    }

    protected fun subscribe(event: Event, action: () -> Unit) {
        disposables += event.subscribe(action)
    }

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean = children.lastOrNull()?.onKeyDown(keyCode, event) ?: false
    open fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean = children.lastOrNull()?.onKeyUp(keyCode, event) ?: false
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