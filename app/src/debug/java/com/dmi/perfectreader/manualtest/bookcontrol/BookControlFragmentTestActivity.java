package com.dmi.perfectreader.manualtest.bookcontrol;

import android.os.Bundle;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.app.App;
import com.dmi.perfectreader.book.BookPresenter;
import com.dmi.perfectreader.bookcontrol.BookControlFragment;
import com.dmi.perfectreader.bookcontrol.BookControlPresenter;
import com.dmi.perfectreader.bookreader.BookReaderPresenter;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.layout.HasLayout;
import com.dmi.util.setting.AbstractSettingsApplier;
import com.dmi.util.setting.SettingListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.ObjectGraph;
import dagger.Provides;
import timber.log.Timber;

@HasLayout(R.layout.activity_container)
public class BookControlFragmentTestActivity extends BaseActivity {
    @Inject
    protected AppSettings appSettings;

    private final SettingsApplier settingsApplier = new SettingsApplier();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(new Module());
        settingsApplier.startListen();
        findOrAddChild(TestFragment.class, R.id.rootContainer);
    }

    @Override
    protected void onDestroy() {
        settingsApplier.stopListen();
        super.onDestroy();
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
            return value -> Timber.d("DDD " + settingName + "=" + value);
        }
    }

    public static class TestFragment extends BookControlFragment {
        @Override
        protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
            return parentGraph.plus(new Module());
        }

        @dagger.Module(addsTo = App.Module.class, injects = {TestFragment.class, BookControlPresenter.class})
        public class Module {
            @Provides
            @Singleton
            public BookReaderPresenter bookReaderPresenter() {
                return new BookReaderPresenter() {
                    @Override
                    public void toggleMenu() {
                        Timber.d("DDD toggleMenu");
                    }

                    @Override
                    public void exit() {
                        Timber.d("DDD exit");
                    }
                };
            }

            @Provides
            @Singleton
            public BookPresenter bookPresenter() {
                return new BookPresenter() {
                    @Override
                    public double currentPercent() {
                        return 0;
                    }

                    @Override
                    public void tap(float x, float y, float tapDiameter, TapHandler tapHandler) {
                        Timber.d("DDD tap (%.2f, %.2f, %.2f)", x, y, tapDiameter);
                        tapHandler.handleTap();
                    }

                    @Override
                    public void goPercent(double percent) {
                        Timber.d("DDD goPercent " + String.format("(%.2f)", percent));
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
        }
    }

    @dagger.Module(addsTo = App.Module.class, injects = BookControlFragmentTestActivity.class)
    public class Module {
    }
}
