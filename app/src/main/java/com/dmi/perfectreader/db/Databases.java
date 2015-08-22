package com.dmi.perfectreader.db;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class Databases {
    private CacheDBOpenHelper cacheDBOpenHelper;
    private UserDataDBOpenHelper userDataDBOpenHelper;

    private final AtomicInteger clientCount = new AtomicInteger(0);

    @Inject
    public Databases(@Named("applicationContext") Context context) {
        cacheDBOpenHelper = new CacheDBOpenHelper(context);
        userDataDBOpenHelper = new UserDataDBOpenHelper(context);
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
        userDataDBOpenHelper.close();
    }

    public synchronized void createOrUpgrade() {
        cacheDBOpenHelper.getWritableDatabase();
        userDataDBOpenHelper.getWritableDatabase();
    }

    public synchronized CacheDBOpenHelper cache() {
        return cacheDBOpenHelper;
    }

    public synchronized UserDataDBOpenHelper userData() {
        return userDataDBOpenHelper;
    }
}
