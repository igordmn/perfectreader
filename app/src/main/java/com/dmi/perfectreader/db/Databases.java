package com.dmi.perfectreader.db;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.concurrent.atomic.AtomicInteger;

@EBean(scope = EBean.Scope.Singleton)
public class Databases {
    @RootContext
    protected Context context;

    private CacheDBOpenHelper cacheDBOpenHelper;

    private final AtomicInteger clientCount = new AtomicInteger(0);

    @AfterInject
    protected void init() {
        cacheDBOpenHelper = new CacheDBOpenHelper(context);
    }

    public void registerClient() {
        clientCount.incrementAndGet();
    }

    public void unregisterClient() {
        if (clientCount.get() > 0) {
            clientCount.decrementAndGet();
            if (clientCount.get() == 0) {
                close();
            }
        }
    }

    private synchronized void close() {
        cacheDBOpenHelper.close();
    }

    public synchronized void createOrUpgrade() {
        cacheDBOpenHelper.getWritableDatabase();
    }

    public synchronized CacheDBOpenHelper cache() {
        return cacheDBOpenHelper;
    }
}
