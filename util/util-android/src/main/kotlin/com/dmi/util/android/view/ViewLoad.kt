package com.dmi.util.android.view

import android.view.View
import androidx.annotation.UiThread
import kotlinx.coroutines.*

class ViewLoad(private val view: View) {
    private var isFinished = false
    private var currentJob: Job? = null
    private var launchJob: (() -> Job)? = null

    init {
        view.addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                if (!isFinished && launchJob != null) {
                    check(currentJob == null)
                    currentJob = launchJob!!()
                }
            }

            override fun onViewDetachedFromWindow(v: View?) {
                currentJob?.cancel()
                currentJob = null
            }
        })
    }

    @UiThread
    fun start(load: suspend () -> Unit) {
        currentJob?.cancel()
        currentJob = null
        isFinished = false
        launchJob = {
            GlobalScope.launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
                load()
                isFinished = true
                launchJob = null
            }
        }
        if (view.isAttachedToWindow)
            currentJob = launchJob!!()
    }

    @UiThread
    fun cancel() {
        currentJob?.cancel()
        currentJob = null
        isFinished = true
    }
}