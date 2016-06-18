package com.dmi.util.system

import rx.lang.kotlin.BehaviorSubject

class ActivityLifeCycle {
    val isResumedObservable = BehaviorSubject(false)

    fun onResume() = isResumedObservable.onNext(true)
    fun onPause() = isResumedObservable.onNext(false)
}