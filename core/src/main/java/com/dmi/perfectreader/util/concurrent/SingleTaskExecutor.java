package com.dmi.perfectreader.util.concurrent;

public class SingleTaskExecutor {
    private int threadPriority = Thread.NORM_PRIORITY;

    private Thread thread;

    public void execute(final InterruptibleRunnable interruptibleRunnable) {
        Thread newThread = new Thread() {
            @Override
            public void run() {
                if (thread != null) {
                    thread.interrupt();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }

                thread = this;

                try {
                    interruptibleRunnable.run();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        };
        newThread.setPriority(threadPriority);
        newThread.start();
    }

    public void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    public void setPriority(int priority) {
        threadPriority = priority;
        if (thread != null) {
            thread.setPriority(priority);
        }
    }
}
