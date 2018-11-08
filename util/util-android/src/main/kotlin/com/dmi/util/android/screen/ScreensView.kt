package com.dmi.util.android.screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import androidx.core.util.set
import com.dmi.util.android.view.restoreState
import com.dmi.util.android.view.saveState
import com.dmi.util.scope.Disposables
import com.dmi.util.screen.Screen
import com.dmi.util.screen.Screens
import java.util.*

@SuppressLint("ViewConstructor")
class ScreensView(
        context: Context,
        private val model: Screens,
        private val screenView: (Screen) -> View
) : FrameLayout(context) {
    private val backstackStates = LinkedList<Bundle?>()
    private var currentRestoredState: Bundle? = null
    private val subscriptions = Disposables()

    private fun currentView(): View? {
        require(childCount <= 1)
        return if (childCount > 0) getChildAt(0) else null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        syncWithModel()

        subscriptions += model.afterGoForward.subscribe {
            val currentView = currentView()
            if (currentView != null) {
                backstackStates.push(currentView.saveState())
                removeView(currentView)
            }
            addView(screenView(model.current!!))
        }

        subscriptions += model.afterGoBackward.subscribe {
            removeView(currentView()!!)
            if (model.size > 0) {
                val view = screenView(model.current!!)
                addView(view)
                val previousState = if (backstackStates.isNotEmpty()) backstackStates.pop() else null
                if (previousState != null)
                    view.restoreState(previousState)
            }
        }

        if (currentRestoredState != null) {
            currentView()?.restoreState(currentRestoredState!!)
            currentRestoredState = null
        }
    }

    private fun syncWithModel() {
        if (model.size > 0 && backstackStates.size + 1 != model.size) {
            backstackStates.clear()
            repeat(model.size - 1) {
                backstackStates.add(null)
            }
        }

        require(childCount == 0)
        val modelCurrent = model.current
        if (modelCurrent != null) {
            val view = screenView(modelCurrent)
            addView(view)
        }
    }

    override fun onDetachedFromWindow() {
        subscriptions.dispose()
        super.onDetachedFromWindow()
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        if (id != NO_ID) {
            container[id] = Bundle().apply {
                putParcelableArray("backstack", backstackStates.toTypedArray())
                putParcelable("current", currentView()?.saveState())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        if (id != NO_ID) {
            val state = container[id] as Bundle?

            this.backstackStates.clear()
            state?.getParcelableArray("backstack")?.forEach {
                this.backstackStates.add(it as Bundle?)
            }

            currentRestoredState = state?.getParcelable<Bundle?>("current")
        }
    }
}