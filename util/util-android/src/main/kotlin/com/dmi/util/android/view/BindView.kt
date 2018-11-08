package com.dmi.util.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.util.set
import org.jetbrains.anko.matchParent
import kotlin.reflect.KProperty0

@SuppressLint("ViewConstructor")
class BindView<M, V: View>(
        context: Context,
        private val model: KProperty0<M?>,
        private val view: ViewBuild.(M, old: V?) -> V,
        private val defferStateRestore: Boolean
) : FrameLayout(context) {
    private var restoredState: Bundle? = null

    init {
        var old: V? = null
        autorun {
            val value = model.get()
            old = if (value == null) {
                if (childCount > 0)
                    removeView(getChildAt(0))
                null
            } else {
                val created = ViewBuild(context).view(value, old)
                created.isSaveFromParentEnabled = false
                if (restoredState != null) {
                    created.restoreState(restoredState!!)
                    restoredState = null
                }

                if (created !== old) {
                    if (childCount > 0)
                        removeView(getChildAt(0))
                    created.layoutParams = params(matchParent, matchParent)
                    addView(created)
                }
                created
            }

            if (!defferStateRestore)
                restoredState = null
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        if (id != NO_ID && childCount > 0) {
            container[id] = getChildAt(0).saveState()
        }
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        if (id != NO_ID) {
            require(childCount == 0)
            restoredState = container[id] as Bundle?
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
        view: ViewBuild.(M, old: V?) -> V,
        /**
         * restore state even if child view created after some time.
         * for example, use this when need restore state of loading view
         */
        defferStateRestore: Boolean = false
): FrameLayout {
    val container = BindView(context, model, view, defferStateRestore)
    container.layoutParams = params
    addView(container)
    return container
}

@JvmName("bindChild2")
fun <M : Any> FrameLayout.bindChild(
        params: FrameLayout.LayoutParams,
        model: KProperty0<M?>,
        view: ViewBuild.(M) -> View,
        defferStateRestore: Boolean = false
): FrameLayout {
    @Suppress("UNUSED_PARAMETER")
    fun ViewBuild.view(model: M, old: View?) = view(model)
    return bindChild(params, model, ViewBuild::view, defferStateRestore)
}

@JvmName("bindChild3")
fun <M : Any, V : View> LinearLayoutCompat.bindChild(
        params: LinearLayoutCompat.LayoutParams,
        model: KProperty0<M?>,
        view: ViewBuild.(M, old: V?) -> V,
        defferStateRestore: Boolean = false
): FrameLayout {
    val container = BindView(context, model, view, defferStateRestore)
    container.layoutParams = params
    addView(container)
    return container
}

@JvmName("bindChild4")
fun <M : Any> LinearLayoutCompat.bindChild(
        params: LinearLayoutCompat.LayoutParams,
        model: KProperty0<M?>,
        view: ViewBuild.(M) -> View,
        defferStateRestore: Boolean = false
): FrameLayout {
    @Suppress("UNUSED_PARAMETER")
    fun ViewBuild.view(model: M, old: View?) = view(model)
    return bindChild(params, model, ViewBuild::view, defferStateRestore)
}