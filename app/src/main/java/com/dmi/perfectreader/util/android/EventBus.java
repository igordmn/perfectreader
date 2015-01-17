package com.dmi.perfectreader.util.android;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

import static com.dmi.perfectreader.util.android.MainThreads.runOnMainThread;

@EBean(scope = EBean.Scope.Singleton)
public class EventBus {
    private Bus bus = new Bus();

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }

    public void postOnMainThread(final Object event) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        });
    }

    public void post(final Object event) {
        bus.post(event);
    }
}
