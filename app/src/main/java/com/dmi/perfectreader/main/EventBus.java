package com.dmi.perfectreader.main;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class EventBus {
    private Bus bus = new Bus();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }

    public void post(final Object event) {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        if (isMainThread) {
            bus.post(event);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    bus.post(event);
                }
            });
        }
    }
}
