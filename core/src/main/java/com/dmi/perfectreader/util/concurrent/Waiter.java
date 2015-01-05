package com.dmi.perfectreader.util.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

public class Waiter {
    private final AtomicBoolean needWakeUp = new AtomicBoolean(false);

    public void request() {
        synchronized (needWakeUp) {
            needWakeUp.set(true);
            needWakeUp.notify();
        }
    }

    public void waitRequest() {
        synchronized (needWakeUp) {
            while (!needWakeUp.get()) {
                try {
                    needWakeUp.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            needWakeUp.set(false);
        }
    }
}
