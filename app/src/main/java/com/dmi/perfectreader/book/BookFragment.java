package com.dmi.perfectreader.book;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.asset.AssetPaths;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.bookview.PageBookBox;
import com.dmi.perfectreader.control.BookControl;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.setting.Settings;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.perfectreader.util.android.EventBus;
import com.dmi.perfectreader.util.android.FragmentExt;
import com.dmi.perfectreader.util.lang.IntegerPercent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

@EFragment(R.layout.fragment_book)
public class BookFragment extends FragmentExt {
    private final static float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

    @FragmentArg
    protected File bookFile;
    @ViewById
    protected PageBookBox bookBox;
    @Bean
    protected AssetPaths assetPaths;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected UserData userData;
    @Bean
    protected Settings settings;
    @Bean
    protected BookControl bookControl;

    @AfterViews
    protected void initViews() {
        bookBox.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        bookBox.setOnTouchListener(bookControl);
        bookBox.setOnLocationChangeListener(new OnLocationChangeListenerImpl());
        bookControl.setBookBox(bookBox);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBook();
    }

    @Background
    protected void loadBook() {
        try {
            bookBox.load(bookFile);
        } catch (IOException e) {
            eventBus.postOnMainThread(new ErrorEvent(e));
        }

        bookBox.configure()
                .setTextAlign(settings.format.TEXT_ALIGN.get())
                .setFontSize(settings.format.FONT_SIZE.get())
                .setLineHeight(settings.format.LINE_HEIGHT.get())
                .commit();

        BookLocation loadedLocation = userData.loadBookLocation(bookFile);
        goLoadedLocation(loadedLocation);
    }

    @UiThread
    protected void goLoadedLocation(BookLocation loadedLocation) {
        if (loadedLocation != null) {
            bookBox.goLocation(loadedLocation);
        } else {
            bookBox.goLocation(new BookLocation(0, IntegerPercent.ZERO));
        }
    }

    public BookLocation currentLocation() {
        return bookBox.currentLocation();
    }

    public void goLocation(BookLocation location) {
        bookBox.goLocation(location);
    }

    public BookLocation percentToLocation(double percent) {
        return bookBox.percentToLocation(percent);
    }

    public double locationToPercent(BookLocation location) {
        return bookBox.locationToPercent(location);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return bookControl.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return bookControl.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    private class OnLocationChangeListenerImpl implements PageBookBox.OnLocationChangeListener {
        @Override
        public void onBookLocationChange() {
            userData.saveBookLocation(bookFile, bookBox.currentLocation());
        }
    }
}
