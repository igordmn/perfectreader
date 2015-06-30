package com.dmi.typoweb;

class BlockingTask implements Runnable {
    private final Runnable runnable;
    private volatile boolean finished;

    public BlockingTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void reset() {
        finished = false;
    }

    @Override
    public void run() {
        runnable.run();
        synchronized (this) {
            finished = true;
            notifyAll();
        }
    }

    public void waitFinished() {
        try {
            synchronized (this) {
                while (!finished) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static abstract class MainExecutor implements Runnable {
        private BlockingTask blockingTask = new BlockingTask(this);

        public void execute() {
            blockingTask.reset();
            TypoWebLibrary.mainThread().postTask(blockingTask);
            blockingTask.waitFinished();
        }
    }
}
