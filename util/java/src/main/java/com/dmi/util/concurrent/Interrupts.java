package com.dmi.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Interrupts {
    public static void checkThreadInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    public static void waitTask(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
