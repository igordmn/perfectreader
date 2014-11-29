package com.dmi.perfectreader.init;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.dmi.perfectreader.main.EventBus;
import com.squareup.otto.Produce;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.concurrent.atomic.AtomicBoolean;

@EBean(scope = EBean.Scope.Singleton)
public class ApplicationInitializer {
    @Bean
    protected EventBus eventBus;
    @Bean
    protected AssetsCopier assetsCopier;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

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
        initFinished.set(true);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                eventBus.post(new ApplicationInitFinishEvent());
            }
        });
    }

    @Produce
    public ApplicationInitFinishEvent produceApplicationInitFinishEvent() {
        return initFinished.get() ? new ApplicationInitFinishEvent() : null;
    }
}
