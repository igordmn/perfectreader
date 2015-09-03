package com.dmi.perfectreader.book;

import android.content.Context;

import com.dmi.perfectreader.book.pagebook.PageBook;
import com.dmi.perfectreader.book.pagebook.PageBookRenderer;
import com.dmi.perfectreader.book.pagebook.WebPageBook;
import com.dmi.perfectreader.book.pagebook.WebPageBookRenderer;
import com.dmi.perfectreader.bookstorage.EPUBBookStorage;
import com.dmi.perfectreader.setting.AppSettings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.base.BasePresenter;
import com.dmi.util.setting.AbstractSettingsApplier;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

import static com.dmi.util.concurrent.Threads.postIOTask;
import static com.dmi.util.concurrent.Threads.postUITask;
import static com.dmi.util.lang.IntegerPercent.ZERO;
import static com.google.common.base.MoreObjects.firstNonNull;

@Singleton
public class BookPresenter extends BasePresenter {
    @Inject
    @Named("bookFile")
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

    @Override
    public void onCreate() {
        settingsApplier.startListen();
        pageBook = new WebPageBook(new WebPageBookClient(), context);
        pageBook.goPercent(loadLocation());
        postIOTask(this::loadBook);
    }

    @Override
    public void onDestroy() {
        pageBook.destroy();
        settingsApplier.stopListen();
    }

    public PageBookRenderer createRenderer() {
        return new WebPageBookRenderer(pageBook);
    }

    protected void loadBook() {
        try {
            bookStorage.load(bookFile);
            postUITask(this::afterBookStorageLoad);
        } catch (IOException e) {
            Timber.e(e, "Load book error");
            view.showBookLoadingError();
        }
    }

    protected void afterBookStorageLoad() {
        settingsApplier.applyAll();
        pageBook.load(bookStorage);
    }

    private int loadLocation() {
        return firstNonNull(userData.loadBookLocation(bookFile), ZERO);
    }

    protected void saveLocation() {
        int currentLocation = pageBook.currentPercent();
        postIOTask(() -> userData.saveBookLocation(bookFile, currentLocation));
    }

    public void resize(int width, int height) {
        pageBook.resize(width, height);
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
        pageBook.tap(x, y, tapDiameter);
    }

    private void handleTap() {
        if (tapHandler != null) {
            tapHandler.handleTap();
            tapHandler = null;
        }
    }

    public void goPercent(int percent) {
        view.reset(() -> {
            pageBook.goPercent(percent);
            saveLocation();
        });
    }

    public void goNextPage() {
        if (pageBook.canGoPage(-view.currentPageRelativeIndex() + 1) != PageBook.CanGoResult.CANNOT) {
            view.goNextPage();
        }
    }

    public void goPreviewPage() {
        if (pageBook.canGoPage(-view.currentPageRelativeIndex() - 1) != PageBook.CanGoResult.CANNOT) {
            view.goPreviewPage();
        }
    }

    public int synchronizeCurrentPage(int currentPageRelativeIndex) {
        if (currentPageRelativeIndex < 0) {
            switch (pageBook.canGoPage(1)) {
                case CAN:
                    pageBook.goNextPage();
                    saveLocation();
                    return currentPageRelativeIndex + 1;
                case CANNOT:
                    return 0;
            }
        } else if (currentPageRelativeIndex > 0) {
            switch (pageBook.canGoPage(-1)) {
                case CAN:
                    pageBook.goPreviewPage();
                    saveLocation();
                    return currentPageRelativeIndex - 1;
                case CANNOT:
                    return 0;
            }
        }
        return currentPageRelativeIndex;
    }

    private class WebPageBookClient implements WebPageBook.Client {
        @Override
        public void afterAnimate() {
            view.refresh();
        }

        @Override
        public void handleTap() {
            BookPresenter.this.handleTap();
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
            listen(appSettings.format.textAlign, value -> pageBook.settings().setTextAlign(value));
            listen(appSettings.format.fontSizePercents, value -> pageBook.settings().setFontSizePercents(value));
            listen(appSettings.format.lineHeightPercents, value -> pageBook.settings().setLineHeightPercents(value));
            listen(appSettings.format.hangingPunctuation, value -> pageBook.settings().setHangingPunctuation(value));
            listen(appSettings.format.hyphenation, value -> pageBook.settings().setHyphenation(value));
        }
    }

    public interface TapHandler {
        void handleTap();
    }
}
