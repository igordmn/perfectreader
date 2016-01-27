package com.dmi.perfectreader.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dmi.perfectreader.BuildConfig;
import com.dmi.perfectreader.bookstorage.EPUBBookStorage;
import com.dmi.perfectreader.cache.BookResourceCache;
import com.dmi.perfectreader.db.Databases;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.Units;
import com.dmi.util.base.BaseApplication;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;
import dagger.Provides;
import timber.log.Timber;

public class App extends BaseApplication {
    @Inject
    protected Databases databases;

    @Override
    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new Module());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLogging();
        initDatabases();
        initUnits();
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }
    }

    private void initDatabases() {
        databases.init();
    }

    private void initUnits() {
        Units.init(this);
    }

    @dagger.Module(library = true, injects = {
            App.class,
            AppSettings.class,
            Databases.class,
            EPUBBookStorage.class,
            BookResourceCache.class,
            UserData.class
    })
    public class Module {
        @Provides
        @Named("applicationContext")
        Context context() {
            return App.this;
        }

        @Provides
        @Named("userDatabase")
        SQLiteDatabase userDatabase(Databases databases) {
            return databases.user();
        }
    }
}
