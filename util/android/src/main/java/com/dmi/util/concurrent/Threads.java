package com.dmi.util.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class Threads {
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public static Future<?> postUITask(Runnable task) {
        FutureTask<Void> futureTask = new FutureTask<Void>(task, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                uiHandler.removeCallbacks(task);
                return super.cancel(mayInterruptIfRunning);
            }
        };
        if (!uiHandler.post(futureTask)) {
            futureTask.cancel(true);
        }
        return futureTask;
    }

    public static Future<?> postIOTask(Runnable task) {
        return ioExecutor.submit(task);
    }
}
