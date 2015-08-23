package com.dmi.perfectreader.db;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class Databases {
    private UserDBOpenHelper userDBOpenHelper;

    private final AtomicInteger clientCount = new AtomicInteger(0);

    @Inject
    public Databases(@Named("applicationContext") Context context) {
        userDBOpenHelper = new UserDBOpenHelper(context);
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
        userDBOpenHelper.close();
    }

    public synchronized void createOrUpgrade() {
        userDBOpenHelper.getWritableDatabase();
    }

    public synchronized UserDBOpenHelper user() {
        return userDBOpenHelper;
    }
}
