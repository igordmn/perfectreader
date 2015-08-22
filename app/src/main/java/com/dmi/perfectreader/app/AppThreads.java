package com.dmi.perfectreader.app;

import com.dmi.util.MainThreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppThreads {
    private static ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public static Future<?> postIOTask(Runnable task) {
        return ioExecutor.submit(task);
    }

    public static void postUITask(Runnable task) {
        MainThreads.post(task);
    }
}
