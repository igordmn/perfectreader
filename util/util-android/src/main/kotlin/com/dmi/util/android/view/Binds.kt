package com.dmi.util.android.view

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onAttachStateChangeListener
import kotlin.reflect.KProperty0

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
                job = launch(UI) {
                    perform()
                    job = null
                }
            }
        }

        fun perform() {
            subscription?.dispose()
            subscription = Scope.onchange(action).subscribe { deffer() }
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
        }
    }
}

/**
 * If model is null, remove child from FrameLayout.
 * If model isn't null, create and add child view.
 * Intercept called scope values when get model value.
 * When any of this values changed, child will be recreated.
 * Returns container for child.
 */
@JvmName("bindChild1")
fun <M : Any, V : View> FrameLayout.bindChild(
        params: FrameLayout.LayoutParams,
        model: KProperty0<M?>,
        view: (context: Context, M, old: V?) -> V
): FrameLayout {
    val container = FrameLayout(context)
    container.layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
    var old: V? = null
    autorun {
        val value = model.get()
        if (value == null) {
            if (old != null)
                container.removeView(old)
            old = null
        } else {
            val created = view(context, value, old)
            created.layoutParams = params
            if (created !== old) {
                if (old != null)
                    container.removeView(old)
                container.addView(created)
            }
            old = created
        }
    }
    addView(container)
    return container
}

@JvmName("bindChild2")
fun <M : Any> FrameLayout.bindChild(
        params: FrameLayout.LayoutParams,
        model: KProperty0<M?>,
        view: (context: Context, M) -> View
): FrameLayout {
    @Suppress("UNUSED_PARAMETER")
    fun view(context: Context, model: M, old: View?) = view(context, model)
    return bindChild(params, model, ::view)
}