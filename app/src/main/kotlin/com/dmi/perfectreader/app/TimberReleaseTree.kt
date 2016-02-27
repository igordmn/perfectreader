package com.dmi.perfectreader.app

import android.util.Log

import timber.log.Timber

class TimberReleaseTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority != Log.VERBOSE && priority != Log.DEBUG) {
            super.log(priority, tag, message, t)
        }
    }
}
