package com.dmi.typoweb;

import android.os.Handler;
import android.os.HandlerThread;

import com.dmi.util.natv.UsedByNative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UsedByNative
class WebThreadImpl {
    private static final Map<Thread, WebThreadImpl> threadToWebThread = new ConcurrentHashMap<>();
    private final long nativeWebThreadImpl;
    private final HandlerThread thread;
    private final Handler handler;
    private final List<Long> nativeTaskObservers = new ArrayList<>();
    private final Map<Long, NativeTaskWrapper> nativeTaskToRunnable = new ConcurrentHashMap<>();

    public static WebThreadImpl current() {
        return threadToWebThread.get(Thread.currentThread());
    }

    @UsedByNative
    private static long nativeCurrent() {
        WebThreadImpl current = current();
        return current != null ? current.nativeWebThreadImpl : 0;
    }

    @UsedByNative
    private static void yieldCurrent() {
        Thread.yield();
    }

    @UsedByNative
    private WebThreadImpl(long nativeWebThreadImpl, String name) {
        this.nativeWebThreadImpl = nativeWebThreadImpl;
        thread = new HandlerThread(name);
        thread.start();
        handler = new Handler(thread.getLooper());
        threadToWebThread.put(thread, this);
    }

    @UsedByNative
    private synchronized void destroy() {
        threadToWebThread.remove(thread);
        ArrayList<NativeTaskWrapper> tasks = new ArrayList<>(nativeTaskToRunnable.values());
        for (NativeTaskWrapper task : tasks) {
            task.cancel();
        }
        thread.quit();
    }

    public void postTask(Runnable runnable) {
        handler.post(runnable);
    }

    public void postDelayedTask(Runnable runnable, long milliseconds) {
        handler.postDelayed(runnable, milliseconds);
    }

    @UsedByNative
    private synchronized void cancelNativeTask(final long nativeTask) {
        NativeTaskWrapper task = nativeTaskToRunnable.get(nativeTask);
        if (task != null) {
            task.cancel();
            handler.removeCallbacks(task);
        }
    }

    @UsedByNative
    private synchronized void postNativeTask(final long nativeTask) {
        handler.post(new NativeTaskWrapper(nativeTask));
    }

    @UsedByNative
    private synchronized void postNativeDelayedTask(final long nativeTask, long milliseconds) {
        handler.postDelayed(new NativeTaskWrapper(nativeTask), milliseconds);
    }

    @UsedByNative
    private boolean isCurrentThread() {
        return Thread.currentThread() == thread;
    }

    @UsedByNative
    private long threadId() {
        return thread.getId();
    }

    @UsedByNative
    private void addNativeTaskObserver(long nativeTaskObserver) {
        nativeTaskObservers.add(nativeTaskObserver);
    }

    @UsedByNative
    private void removeNativeTaskObserver(long nativeTaskObserver) {
        nativeTaskObservers.remove(nativeTaskObserver);
    }

    private native void nativeRunTask(long nativeTask);
    private native void nativeDeleteTask(long nativeTask);
    private native void nativeWillProcessTask(long nativeTaskObserver);
    private native void nativeDidProcessTask(long nativeTaskObserver);

    private class NativeTaskWrapper implements Runnable {
        private final long nativeTask;
        private boolean finished = false;

        public NativeTaskWrapper(long nativeTask) {
            this.nativeTask = nativeTask;
            nativeTaskToRunnable.put(nativeTask, this);
        }

        public synchronized void cancel() {
            if (!finished) {
                finished = true;
                nativeDeleteTask(nativeTask);
                nativeTaskToRunnable.remove(nativeTask);
            }
        }

        @Override
        public synchronized void run() {
            if (!finished) {
                finished = true;
                for (long nativeTaskObserver : nativeTaskObservers) {
                    nativeWillProcessTask(nativeTaskObserver);
                }
                nativeRunTask(nativeTask);
                nativeDeleteTask(nativeTask);
                nativeTaskToRunnable.remove(nativeTask);
                for (long nativeTaskObserver : nativeTaskObservers) {
                    nativeDidProcessTask(nativeTaskObserver);
                }
            }
        }
    }
}
