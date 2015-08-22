package com.dmi.perfectreader.book;

import android.content.Context;

import com.dmi.perfectreader.bookstorage.EPUBBookStorage;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.base.BasePresenter;
import com.dmi.util.lang.IntegerPercent;
import com.dmi.util.setting.AbstractSettingsApplier;
import com.dmi.util.setting.SettingListener;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

import static com.dmi.perfectreader.app.AppThreads.postIOTask;
import static com.dmi.perfectreader.app.AppThreads.postUITask;

@Singleton
public class BookPresenter extends BasePresenter {
    protected File bookFile;

    @Inject
    @Named("applicationContext")
    protected Context context;
    @Inject
    protected UserData userData;
    @Inject
    protected AppSettings appSettings;
    @Inject
    protected EPUBBookStorage bookStorage;
    @Inject
    protected BookFragment view;

    private final SettingsApplier settingsApplier = new SettingsApplier();
    private WebPageBook pageBook;
    private TapHandler tapHandler = null;

    private boolean bookLoaded = false;

    @Override
    public void onCreate() {
        settingsApplier.startListen();
        pageBook = new WebPageBook(new WebPageBookClient(), context);
    }

    @Override
    public void onDestroy() {
        pageBook.destroy();
        settingsApplier.stopListen();
    }

    public void setBookFile(File bookFile) {
        this.bookFile = bookFile;
    }

    public void requestBook() {
        view.init(pageBook);
        if (!bookLoaded) {
            pageBook.goPercent(loadLocation());
            postIOTask(this::loadBook);
            bookLoaded = true;
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

    protected void loadBook() {
        try {
            bookStorage.load(bookFile);
            postUITask(this::initBook);
        } catch (IOException e) {
            Timber.e(e, "Load book error");
            view.showBookLoadingError();
        }
    }

    protected void initBook() {
        view.queueEvent(() -> {
            settingsApplier.applyAll();
            pageBook.load(bookStorage);
            pageBook.goPercent(loadLocation());
        });
    }

    private void saveLocation(int integerPercent) {
        userData.saveBookLocation(bookFile, integerPercent);
    }

    // todo если во время поворота pageBookView уничтожится, то действие не сработает
    // сделать очередь из одного элемента. если при добавлении задания, в очереди что-то есть, очищать очередь
    protected void saveCurrentLocation() {
        view.queueEvent(() -> saveLocation(pageBook.currentPercent()));
    }

    public void resume() {
        pageBook.resume();
    }

    public void pause() {
        pageBook.pause();
    }

    public int currentPercent() {
        return pageBook.currentPercent();
    }

    public void tap(float x, float y, float tapDiameter, TapHandler tapHandler) {
        this.tapHandler = tapHandler;
        view.queueEvent(() -> pageBook.tap(x, y, tapDiameter));
    }

    // todo если во время поворота pageBookView уничтожится, то действие не сработает
    private void handleTap() {
        if (tapHandler != null) {
            tapHandler.handleTap();
            tapHandler = null;
        }
    }

    public void goPercent(int percent) {
        view.goPercent(percent);
    }

    public void goNextPage() {
        view.goNextPage();
    }

    public void goPreviewPage() {
        view.goPreviewPage();
    }

    private class WebPageBookClient implements WebPageBook.Client {
        @Override
        public void afterAnimate() {
            view.refresh();
        }

        @Override
        public void afterLocationChanged() {
            postUITask(BookPresenter.this::saveCurrentLocation);
        }

        @Override
        public void handleTap() {
            postUITask(BookPresenter.this::handleTap);
        }
    }

    private class SettingsApplier extends AbstractSettingsApplier {
        public void applyAll() {
            WebPageBook.Settings settings = pageBook.settings();
            settings.setTextAlign(appSettings.format.textAlign.get());
            settings.setFontSizePercents(appSettings.format.fontSizePercents.get());
            settings.setLineHeightPercents(appSettings.format.lineHeightPercents.get());
            settings.setHangingPunctuation(appSettings.format.hangingPunctuation.get());
            settings.setHyphenation(appSettings.format.hyphenation.get());
        }

        @Override
        protected void listen() {
            listenWrapped(appSettings.format.textAlign, value -> pageBook.settings().setTextAlign(value));
            listenWrapped(appSettings.format.fontSizePercents, value -> pageBook.settings().setFontSizePercents(value));
            listenWrapped(appSettings.format.lineHeightPercents, value -> pageBook.settings().setLineHeightPercents(value));
            listenWrapped(appSettings.format.hangingPunctuation, value -> pageBook.settings().setHangingPunctuation(value));
            listenWrapped(appSettings.format.hyphenation, value -> pageBook.settings().setHyphenation(value));
        }

        private <T> void listenWrapped(AppSettings.Setting<T> setting, SettingListener<T> listener) {
            listen(setting, wrap(listener));
        }

        private <T> SettingListener<T> wrap(SettingListener<T> listener) {
            return value -> view.queueEvent(() -> listener.onValueSet(value));
        }
    }

    public interface TapHandler {
        void handleTap();
    }
}
