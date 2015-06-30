package com.dmi.util;

import android.os.Handler;
import android.os.Looper;

import static com.google.common.base.Preconditions.checkState;

public abstract class MainThreads {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        if (isMainThread) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    public static void post(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void removeCallbacks(Runnable runnable) {
        mainHandler.removeCallbacks(runnable);
    }

    public static void checkInMainThread() {
        checkState(Looper.getMainLooper() == Looper.myLooper());
    }
}
