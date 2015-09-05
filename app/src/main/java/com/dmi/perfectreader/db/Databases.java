package com.dmi.perfectreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.util.db.DatabaseUpgrades;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;
import static com.dmi.util.db.DatabaseUpgrades.upgradeDatabase;

@Singleton
public class Databases {
    private SQLiteDatabase user;

    @Named("applicationContext")
    @Inject
    Context context;

    @Override
    protected void finalize() throws Throwable {
        try {
            // it's safe keep databases opened during application life. @see http://stackoverflow.com/questions/6608498/best-place-to-close-database-connection
            close();
        } finally {
            super.finalize();
        }
    }

    public synchronized void init() {
        user = context.openOrCreateDatabase("user", MODE_PRIVATE, null);
        try {
            upgradeDatabase(context, user, "db/user");
        } catch (DatabaseUpgrades.DowngradeException e) {
            throw new RuntimeException(e); // todo показывать диалог
        }
    }

    private void close() {
        user.close();
    }

    public synchronized SQLiteDatabase user() {
        return user;
    }
}
