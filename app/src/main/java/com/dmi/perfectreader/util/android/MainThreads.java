package com.dmi.perfectreader.util.android;

import android.os.Handler;
import android.os.Looper;

public abstract class MainThreads {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        if (isMainThread) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }
}
