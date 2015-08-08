package com.dmi.perfectreader.manualtest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.control.BookControlFragment;
import com.dmi.perfectreader.control.BookControlFragment_;
import com.dmi.perfectreader.facade.BookFacade;
import com.dmi.perfectreader.facade.BookReaderFacade;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.util.AppCompatActivityExt;
import com.dmi.util.lang.IntegerPercent;
import com.dmi.util.setting.AbstractSettingsApplier;
import com.dmi.util.setting.SettingListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import timber.log.Timber;

@EActivity(R.layout.activity_fragment_container)
public class BookControlFragmentTestActivity extends AppCompatActivityExt implements BookReaderFacade {
    @Bean
    protected AppSettings appSettings;

    private final SettingsApplier settingsApplier = new SettingsApplier();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsApplier.startListen();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        settingsApplier.stopListen();
        super.onDestroy();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = BookFragment.class.getName();
        BookControlFragment fragment = (BookControlFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = BookControlFragment_.builder().build();
            fragmentManager.beginTransaction().add(R.id.mainContainer, fragment, tag).commit();
        }
    }

    @Override
    public BookFacade book() {
        return new BookFacade() {
            @Override
            public int currentPercent() {
                return 0;
            }

            @Override
            public void tap(float x, float y, float tapDiameter, TapHandler tapHandler) {
                Timber.d("DDD tap (%.2f, %.2f, %.2f)", x, y, tapDiameter);
                tapHandler.handleTap();
            }

            @Override
            public void goPercent(int percent) {
                Timber.d("DDD goPercent " + String.format("(%.2f)",IntegerPercent.toDouble(percent)));
            }

            @Override
            public void goNextPage() {
                Timber.d("DDD goNextPage");
            }

            @Override
            public void goPreviewPage() {
                Timber.d("DDD goPreviewPage");
            }
        };
    }

    @Override
    public void toggleMenu() {
        Timber.d("DDD toggleMenu");
    }

    @Override
    public void exit() {
        Timber.d("DDD exit");
    }

    private class SettingsApplier extends AbstractSettingsApplier {
        @Override
        protected void listen() {
            listen(appSettings.format.fontSizePercents);
            listen(appSettings.format.lineHeightPercents);
        }

        private <T> void listen(AppSettings.Setting<T> setting) {
            listen(setting, listener(setting.name()));
        }

        private <T> SettingListener<T> listener(String settingName) {
            return value -> Timber.d("DDD" + settingName + "=" + value);
        }
    }
}
