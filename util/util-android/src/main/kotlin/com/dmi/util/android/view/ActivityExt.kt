package com.dmi.util.android.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dmi.util.scope.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

private typealias Listener = () -> Unit
private typealias ListenerArg<T> = (T) -> Unit

abstract class ActivityExt<M : Disposable> protected constructor() : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var onWindowFocusChanged: ListenerArg<Boolean>? = null
    private var onUserInteraction: Listener? = null
    protected lateinit var model: M
    protected lateinit var view: View

    protected abstract fun createModel(stateData: ByteArray?): M
    protected abstract fun saveModel(model: M): ByteArray
    protected abstract fun ViewBuild.view(model: M): View

    protected fun recreateModel() {
        model.dispose()
        model = createModel(null)
        setContentView(ViewBuild(this).view(model))
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        job = Job()
        super.onCreate(savedInstanceState)
        model = createModel(savedInstanceState?.getByteArray("model"))
        view = ViewBuild(this).view(model)
        val viewState = savedInstanceState?.getBundle("view")
        viewState?.let(view::restoreState)
        setContentView(view)
    }

    override fun onDestroy() {
        model.dispose()
        job.cancel()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putByteArray("model", saveModel(model))
        outState.putBundle("view", view.saveState())
    }

    fun onWindowFocusChanged(listener: ListenerArg<Boolean>) {
        onWindowFocusChanged = listener
    }

    fun onUserInteraction(listener: Listener) {
        onUserInteraction = listener
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        onWindowFocusChanged?.invoke(hasFocus)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        onUserInteraction?.invoke()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return window.decorView.interceptKeys(event) || super.dispatchKeyEvent(event)
    }

    private fun View.interceptKeys(event: KeyEvent): Boolean {
        if (this is ViewGroup) {
            for (i in childCount - 1 downTo 0) {
                if (getChildAt(i).interceptKeys(event))
                    return true
            }
        }
        if (this is KeyInterceptable) {
            if (onInterceptKey(event))
                return true
        }
        return false
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val focus = currentFocus
        return if (
                event.action == MotionEvent.ACTION_DOWN &&
                focus != null &&
                focus is ClearFocusOnClickOutside &&
                event !in focus
        ) {
            focus.clearFocus()
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }
}