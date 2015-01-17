package com.dmi.perfectreader.init;

import android.content.Context;
import android.os.AsyncTask;

import com.dmi.perfectreader.asset.AssetsCopier;
import com.dmi.perfectreader.db.Databases;
import com.dmi.perfectreader.util.android.EventBus;
import com.dmi.perfectreader.util.android.Units;
import com.squareup.otto.Produce;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.dmi.perfectreader.util.android.MainThreads.runOnMainThread;

@EBean(scope = EBean.Scope.Singleton)
public class ApplicationInitializer {
    @Bean
    protected EventBus eventBus;
    @Bean
    protected AssetsCopier assetsCopier;
    @Bean
    protected Databases databases;
    @RootContext
    protected Context context;

    @AfterInject
    protected void initThis() {
        eventBus.register(this);
    }

    private final AtomicBoolean initFinished = new AtomicBoolean(false);

    public void runInit() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                initApp();
                return null;
            }
        }.execute();
    }

    private void initApp() {
        assetsCopier.copyAssets();
        databases.createOrUpgrade();
        Units.init(context);
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                eventBus.post(new ApplicationInitFinishEvent());
                initFinished.set(true);
            }
        });
    }

    @Produce
    public ApplicationInitFinishEvent produceApplicationInitFinishEvent() {
        return initFinished.get() ? new ApplicationInitFinishEvent() : null;
    }
}
