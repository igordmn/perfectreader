package com.dmi.perfectreader.bookreader;

import com.dmi.perfectreader.db.Databases;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.base.BasePresenter;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BookReaderPresenter extends BasePresenter {
    @Inject
    protected UserData userData;
    @Inject
    protected Databases databases;
    @Inject
    protected BookReaderFragment view;

    @Override
    protected void onCreate() {
        databases.registerClient();
    }

    @Override
    protected void onDestroy() {
        databases.unregisterClient();
    }

    public void openBook(File requestedBookFile) {
        File bookFile = getBookFile(requestedBookFile);
        if (bookFile != null) {
            view.openBook(bookFile);
        } else {
            view.showNeedOpenThroughFileManager();
        }
    }

    private File getBookFile(File requestedBookFile) {
        if (requestedBookFile != null) {
            userData.saveLastBookFile(requestedBookFile);
            return requestedBookFile;
        } else {
            return userData.loadLastBookFile();
        }
    }

    public void toggleMenu() {
        view.toggleMenu();
    }

    public void exit() {
        view.exit();
    }
}
