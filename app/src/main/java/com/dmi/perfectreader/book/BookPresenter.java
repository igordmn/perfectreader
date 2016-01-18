package com.dmi.perfectreader.book;

import android.content.Context;

import com.dmi.perfectreader.book.pagebook.PageBook;
import com.dmi.perfectreader.book.pagebook.PageBookRenderer;
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
    private PageBook pageBook;
    private TapHandler tapHandler = null;

    @Override
    public void onCreate() {
        settingsApplier.startListen();
        pageBook.goPercent(loadLocation());
        postIOTask(this::loadBook);
    }

    @Override
    public void onDestroy() {
        settingsApplier.stopListen();
    }

    public PageBookRenderer createRenderer() {
        return null;
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
    }

    private int loadLocation() {
        return firstNonNull(userData.loadBookLocation(bookFile), ZERO);
    }

    protected void saveLocation() {
        int currentLocation = 0;
        postIOTask(() -> userData.saveBookLocation(bookFile, currentLocation));
    }

    public void resize(int width, int height) {
        pageBook.resize(width, height);
    }

    public void resume() {
    }

    public void pause() {
    }

    public int currentPercent() {
        return 0;
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

    private class SettingsApplier extends AbstractSettingsApplier {
        public void applyAll() {
        }

        @Override
        protected void listen() {
        }
    }

    public interface TapHandler {
        void handleTap();
    }
}
