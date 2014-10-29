package com.dmi.perfectreader.util.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

public class Waiter {
    private final AtomicBoolean needWakeUp = new AtomicBoolean(false);

    public void wakeUp() {
        synchronized (needWakeUp) {
            needWakeUp.set(true);
            needWakeUp.notify();
        }
    }

    public void waitForWakeUp() {
        synchronized (needWakeUp) {
            while (!needWakeUp.get()) {
                try {
                    needWakeUp.wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
            needWakeUp.set(false);
        }
    }
}
