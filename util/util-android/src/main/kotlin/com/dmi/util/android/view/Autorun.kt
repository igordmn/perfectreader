package com.dmi.util.android.view

import android.view.View
import com.dmi.util.scope.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Call action and intercept all called scope values.
 * When any of this values changed, action will dispatched to run.
 */
fun View.autorun(action: () -> Unit) {
    fun subscribe() = object : Disposable {
        var subscription: Disposable? = null
        var job: Job? = null

        init {
            perform()
        }

        fun deffer() {
            if (job == null) {
                job = GlobalScope.launch(Dispatchers.Main) {
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

    var subscription: Disposable? = null
    if (isAttachedToWindow)
        subscription = subscribe()

    addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            check(subscription == null)
            subscription = subscribe()
        }

        override fun onViewDetachedFromWindow(v: View?) {
            subscription!!.dispose()
            subscription = null
        }
    })
}

fun <T : Any> View.subscribe(list: ObservableList<T>, afterAdd: (item: T) -> Unit, afterRemove: () -> Unit) {
    val subscriptions = Disposables()
    val subscription = subscriptions.debugIfEnabled()

    check(!isAttachedToWindow)

    addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            list.forEach(afterAdd)
            subscriptions += list.afterAdd.subscribe {
                afterAdd(list.top!!)
            }
            subscriptions += list.afterRemove.subscribe {
                afterRemove()
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            subscription.dispose()
        }
    })
}