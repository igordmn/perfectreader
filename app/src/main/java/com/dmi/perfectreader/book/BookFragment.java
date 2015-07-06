package com.dmi.perfectreader.book;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.bookstorage.EPUBBookStorage;
import com.dmi.perfectreader.facade.BookFacade;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.FragmentExt;
import com.dmi.util.lang.IntegerPercent;
import com.dmi.util.setting.AbstractSettingsApplier;
import com.dmi.util.setting.SettingListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import static android.util.Log.getStackTraceString;

@EFragment(R.layout.fragment_book)
public class BookFragment extends FragmentExt implements BookFacade {
    private static final String LOG_TAG = BookFragment.class.getName();
    private static final float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

    @FragmentArg
    protected File bookFile;

    @ViewById
    protected PageBookView pageBookView;

    @Bean
    protected UserData userData;
    @Bean
    protected AppSettings appSettings;
    @Bean
    protected EPUBBookStorage bookStorage;

    private final SettingsApplier settingsApplier = new SettingsApplier();
    private WebPageBook pageBook;
    private BookFacade.TapHandler tapHandler = null;

    @AfterInject
    protected void init() {
        setRetainInstance(true);
    }

    @AfterViews
    protected void initViews() {
        pageBookView.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        pageBookView.setPageBook(pageBook);
        pageBookView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pageBookView.queueEvent(
                        () -> pageBook.tap(event.getX(), event.getY(), event.getTouchMajor())
                );
            }
            return true;
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsApplier.startListen();
        pageBook = new WebPageBook(new WebPageBookClient(), getActivity());
        if (bookFile != null) {
            pageBook.goPercent(loadLocation());
            loadBook();
        } else {
            Toast.makeText(getActivity(), R.string.bookNotLoaded, Toast.LENGTH_SHORT).show();
        }
    }

    private int loadLocation() {
        Integer savedLocation = userData.loadBookLocation(bookFile);
        if (savedLocation != null) {
            return savedLocation;
        } else {
            return IntegerPercent.ZERO;
        }
    }

    @Background
    protected void loadBook() {
        try {
            bookStorage.load(bookFile);
            initBook();
        } catch (IOException e) {
            Log.e(LOG_TAG, getStackTraceString(e));
            Toast.makeText(getActivity(), R.string.bookOpenError, Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    protected void initBook() {
        pageBookView.queueEvent(() -> {
            settingsApplier.applyAll();
            pageBook.load(bookStorage);
            pageBook.goPercent(loadLocation());
        });
    }

    private void saveLocation(int integerPercent) {
        userData.saveBookLocation(bookFile, integerPercent);
    }

    // todo если во время поворота pageBookView унижтожится, то действие не сработает
    // сделать очередь из одного элемента. если при добавлении задания, в очереди что-то есть, очищать очередь
    @UiThread
    protected void saveCurrentLocation() {
        pageBookView.queueEvent(() -> saveLocation(pageBook.currentPercent()));
    }

    @Override
    public void onDestroy() {
        settingsApplier.stopListen();
        pageBook.destroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        pageBook.resume();
        pageBookView.onResume();
    }

    @Override
    public void onPause() {
        pageBookView.onPause();
        pageBook.pause();
        super.onPause();
    }

    @Override
    public int currentPercent() {
        return pageBook.currentPercent();
    }

    @Override
    public void tap(float x, float y, float tapDiameter, BookFacade.TapHandler tapHandler) {
        pageBookView.queueEvent(() -> {
            this.tapHandler = tapHandler;
            pageBook.tap(x, y, tapDiameter);
        });
    }

    // todo если во время поворота pageBookView унижтожится, то действие не сработает
    private void handleTap() {
        pageBookView.queueEvent(() -> {
            if (tapHandler != null) {
                tapHandler.handleTap();
                tapHandler = null;
            }
        });
    }

    @Override
    public void goPercent(int percent) {
        pageBookView.goPercent(percent);
    }

    @Override
    public void goNextPage() {
        pageBookView.goNextPage();
    }

    @Override
    public void goPreviewPage() {
        pageBookView.goPreviewPage();
    }

    private class WebPageBookClient implements WebPageBook.Client {
        @Override
        public void afterAnimate() {
            if (pageBookView != null) {
                pageBookView.refresh();
            }
        }

        @Override
        public void afterLocationChanged() {
            saveCurrentLocation();
        }

        @Override
        public void handleTap() {
            BookFragment.this.handleTap();
        }
    }

    private class SettingsApplier extends AbstractSettingsApplier {
        public void applyAll() {
            WebPageBook.Settings settings = pageBook.settings();
            settings.setTextAlign(appSettings.format.textAlign.get());
            settings.setFontSizePercents(appSettings.format.fontSizePercents.get());
            settings.setLineHeightPercents(appSettings.format.lineHeightPercents.get());
            settings.setHangingPunctuation(appSettings.format.hangingPunctuation.get());
        }

        @Override
        protected void listen() {
            listenWrapped(appSettings.format.textAlign, value -> pageBook.settings().setTextAlign(value));
            listenWrapped(appSettings.format.fontSizePercents, value -> pageBook.settings().setFontSizePercents(value));
            listenWrapped(appSettings.format.lineHeightPercents, value -> pageBook.settings().setLineHeightPercents(value));
            listenWrapped(appSettings.format.hangingPunctuation, value -> pageBook.settings().setHangingPunctuation(value));
        }

        private <T> void listenWrapped(AppSettings.Setting<T> setting, SettingListener<T> listener) {
            listen(setting, wrap(listener));
        }

        private <T> SettingListener<T> wrap(SettingListener<T> listener) {
            return value -> pageBookView.queueEvent(() -> listener.onValueSet(value));
        }
    }
}
