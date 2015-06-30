package com.dmi.util.concurrent;

public abstract class Interrupts {
    public static void checkThreadInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }
}