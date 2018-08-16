package com.dmi.util.android.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
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
@JvmName("autochild1")
fun <M : Any, V : View> FrameLayout.bindChild(
        model: KProperty0<M?>,
        view: (M, old: V?) -> V,
        params: ViewGroup.LayoutParams,
        init: FrameLayout.() -> Unit = {}
): FrameLayout {
    val container = FrameLayout(context)
    container.layoutParams = params
    var old: V? = null
    autorun {
        val value = model.get()
        if (value == null) {
            if (old != null)
                container.removeView(old)
            old = null
        } else {
            val created = view(value, old)
            created.layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
            if (created !== old) {
                if (old != null)
                    container.removeView(old)
                container.addView(created)
            }
            old = created
        }
    }
    init(container)
    addView(container)
    return container
}

@JvmName("autochild2")
fun <M : Any, V : View> FrameLayout.bindChild(
        model: KProperty0<M?>,
        view: Context.(M, old: V?) -> V,
        params: ViewGroup.LayoutParams,
        init: FrameLayout.() -> Unit = {}
): FrameLayout {
    fun view(model: M, old: V?): V = context.view(model, old)
    return bindChild(model, ::view, params, init)
}

@JvmName("autochild3")
fun <M : Any> FrameLayout.bindChild(
        model: KProperty0<M?>,
        view: (M) -> View,
        params: ViewGroup.LayoutParams,
        init: FrameLayout.() -> Unit = {}
): FrameLayout {
    @Suppress("UNUSED_PARAMETER")
    fun view(model: M, old: View?) = view(model)
    return bindChild(model, ::view, params, init)
}

@JvmName("autochild4")
fun <M : Any> FrameLayout.bindChild(
        model: KProperty0<M?>,
        view: Context.(M) -> View,
        params: ViewGroup.LayoutParams,
        init: FrameLayout.() -> Unit = {}
): FrameLayout {
    @Suppress("UNUSED_PARAMETER")
    fun Context.view(model: M, old: View?) = view(model)
    return bindChild(model, Context::view, params, init)
}