package com.dmi.perfectreader.app;

import android.util.Log;

import timber.log.Timber;

public class TimberReleaseTree extends Timber.DebugTree {
    @Override protected void log(int priority, String tag, String message, Throwable t) {
        if (priority != Log.VERBOSE && priority != Log.DEBUG) {
            super.log(priority, tag, message, t);
        }
    }
}
