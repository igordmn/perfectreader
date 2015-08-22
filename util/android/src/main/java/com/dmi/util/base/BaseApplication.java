package com.dmi.util.base;

import android.app.Application;

import dagger.ObjectGraph;

public class BaseApplication extends Application {
    private ObjectGraph objectGraph;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = createObjectGraph();
        if (objectGraph != null) {
            objectGraph.inject(this);
        }
    }

    protected ObjectGraph createObjectGraph() {
        return null;
    }

    protected ObjectGraph objectGraph() {
        return objectGraph;
    }
}
