package com.dmi.util.android.view

import android.view.View
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.onchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.onAttachStateChangeListener

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

    onAttachStateChangeListener {
        onViewAttachedToWindow {
            check(subscription == null)
            subscription = subscribe()
        }

        onViewDetachedFromWindow {
            subscription!!.dispose()
            subscription = null
        }
    }
}