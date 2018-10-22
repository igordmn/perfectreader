package com.dmi.util.android.view

import android.view.View
import androidx.annotation.UiThread
import kotlinx.coroutines.*
import org.jetbrains.anko.onAttachStateChangeListener

class ViewLoad(private val view: View) {
    private var isFinished = false
    private var currentJob: Job? = null
    private var launchJob: (() -> Job)? = null

    init {
        view.onAttachStateChangeListener {
            onViewAttachedToWindow {
                if (!isFinished && launchJob != null) {
                    check(currentJob == null)
                    currentJob = launchJob!!()
                }
            }

            onViewDetachedFromWindow {
                currentJob?.cancel()
                currentJob = null
            }
        }
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
}