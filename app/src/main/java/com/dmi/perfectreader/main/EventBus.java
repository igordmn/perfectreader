package com.dmi.perfectreader.main;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class EventBus {
    private Bus bus = new Bus();

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }

    public void post(Object event) {
        bus.post(event);
    }
}
