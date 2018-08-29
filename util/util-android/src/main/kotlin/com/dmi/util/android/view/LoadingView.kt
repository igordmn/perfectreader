package com.dmi.util.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import org.jetbrains.anko.wrapContent

@SuppressLint("ViewConstructor")
class LoadingView<T : View>(context: Context, private val child: T, showProgress: Boolean = false) : FrameLayout(context) {
    private val progressBar:ProgressBar? = if (showProgress) {
        child(params(wrapContent, wrapContent), ProgressBar(context).apply {
            visibility = View.INVISIBLE
        })
    } else {
        null
    }
    private var currentJob: Job? = null
    private var launchJob: (() -> Job)? = null
    private var isFinished = false

    init {
        child.visibility = View.VISIBLE
        addView(child)
    }

    fun load(load: suspend T.() -> Unit) {
        child.visibility = View.INVISIBLE
        progressBar?.visibility = View.VISIBLE
        currentJob?.cancel()
        currentJob = null
        isFinished = false
        launchJob = {
            launch(UI, start = CoroutineStart.UNDISPATCHED) {
                child.load()
                child.visibility = View.VISIBLE
                progressBar?.visibility = View.INVISIBLE
                isFinished = true
                launchJob = null
            }
        }
        if (isAttachedToWindow)
            currentJob = launchJob!!()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isFinished) {
            check(currentJob == null)
            currentJob = launchJob!!()
        }
    }

    override fun onDetachedFromWindow() {
        currentJob?.cancel()
        currentJob = null
        super.onDetachedFromWindow()
    }
}