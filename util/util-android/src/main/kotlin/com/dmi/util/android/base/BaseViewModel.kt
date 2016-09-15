package com.dmi.util.android.base

import android.os.Bundle
import android.support.annotation.CallSuper
import com.dmi.util.android.persist.StateSaver
import com.dmi.util.lang.returnValue
import com.dmi.util.refWatcher
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.io.Serializable
import java.util.*

abstract class BaseViewModel {
    private val subscriptions = CompositeSubscription()
    private val propertyStateSaver = StateSaver("__PROPERTIES")
    private val children = LinkedHashSet<BaseViewModel>()
    private var restoredState: Bundle? = null

    fun <T : Serializable?> saveState(initial: T) = propertyStateSaver.register(initial)

    protected fun <T : BaseViewModel> initChild(child: T): T {
        restoredState?.let {
            child.restore(stateForChild(it, children.size))
        }
        return addChild(child)
    }

    protected fun <T : BaseViewModel> addChild(child: T): T {
        children.add(child)
        return child
    }

    protected fun <T : BaseViewModel> removeChild(child: T) {
        child.destroy()
        children.remove(child)
    }

    protected fun <T : BaseViewModel> addOrRemoveChild(condition: Boolean, current: T?, create: () -> T): T? {
        if (condition && current == null)
            return addChild(create())

        if (!condition && current != null) {
            removeChild(current)
            return null
        }

        return current
    }

    protected fun <T : BaseViewModel> BaseViewModel.toggleChild(current: T?, create: () -> T) =
            if (current == null) {
                addChild(create())
            } else {
                removeChild(current).returnValue(null)
            }

    @CallSuper
    open fun destroy() {
        children.forEach { it.destroy() }
        subscriptions.clear()
        refWatcher.watch(this)
    }

    @CallSuper
    open fun restore(state: Bundle) {
        restoredState = state
        propertyStateSaver.restore(state)
        children.forEachIndexed { i, it -> it.restore(stateForChild(state, i)) }
    }

    @CallSuper
    open fun save(state: Bundle) {
        children.forEachIndexed { i, it -> it.save(stateForChild(state, i)) }
        propertyStateSaver.save(state)
    }

    protected fun <T> subscribe(observable: Observable<T>, onNext: (T) -> Unit) =
            subscriptions.add(observable.subscribe(onNext))

    private fun stateForChild(state: Bundle, index: Int): Bundle {
        val key = "__CHILD$index"
        var bundle = state.getBundle(key)
        if (bundle == null) {
            bundle = Bundle()
            state.putBundle(key, bundle)
        }
        return bundle
    }
}