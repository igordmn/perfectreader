package com.dmi.perfectreader.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.BookFragment_;
import com.dmi.perfectreader.db.Databases;
import com.dmi.perfectreader.error.BookFileNotFoundException;
import com.dmi.perfectreader.error.BookInvalidException;
import com.dmi.perfectreader.error.ErrorEvent;
import com.dmi.perfectreader.init.ApplicationInitFinishEvent;
import com.dmi.perfectreader.menu.MenuFragment;
import com.dmi.perfectreader.menu.MenuFragment_;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.perfectreader.util.android.ActionBarActivityExt;
import com.dmi.perfectreader.util.android.EventBus;
import com.google.common.base.Objects;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@EActivity(R.layout.activity_book_reader)
public class MainActivity extends ActionBarActivityExt {
    @ViewById
    protected View bookNotLoadedView;
    @ViewById
    protected View bookOpenErrorView;
    @ViewById
    protected TextView bookOpenErrorDetailTextView;
    @Bean
    protected UserData userData;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected Databases databases;

    @InstanceState
    protected boolean bookFileRecognized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        databases.registerClient();
    }

    @Subscribe
    public void onApplicationInitFinish(ApplicationInitFinishEvent event) {
        if (!bookFileRecognized) {
            openBook(getBookFile());
            bookFileRecognized = true;
        }
    }

    private File getBookFile() {
        File bookFile = userData.loadLastBookFile();
        File intentBookFile = bookFileFromIntent();
        if (intentBookFile != null && !Objects.equal(bookFile, intentBookFile)) {
            bookFile = intentBookFile;
            userData.saveLastBookFile(bookFile);
        }
        return bookFile;
    }

    private void openBook(File bookFile) {
        if (bookFile != null) {
            BookFragment bookFragment = BookFragment_.builder()
                    .bookFile(bookFile)
                    .build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainContainer, bookFragment, BookFragment.class.getName())
                    .commit();
        } else {
            bookNotLoadedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        databases.unregisterClient();
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onToggleMenuIntent(ToggleMenuIntent intent) {
        toggleMenu();
    }

    @Subscribe
    public void onGoBackIntent(GoBackIntent intent) {
        goBack();
    }

    private void toggleMenu() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MenuFragment.class.getName();
        MenuFragment fragment = (MenuFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = MenuFragment_.builder().build();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .add(R.id.mainContainer, fragment, tag)
                    .commit();
        } else {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .remove(fragment)
                    .commit();
        }
    }

    private void goBack() {
        finish();
    }

    private BookFragment bookFragment() {
        return (BookFragment) getSupportFragmentManager().findFragmentByTag(BookFragment.class.getName());
    }

    private File bookFileFromIntent() {
        Uri data = getIntent().getData();
        try {
            if (data != null) {
                String path = URLDecoder.decode(data.getEncodedPath(), "UTF-8");
                return new File(path);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent) {
        showError(getMessage(errorEvent.getThrowable()));
    }

    private void showError(String message) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final BookFragment bookFragment = bookFragment();
        if (bookFragment != null) {
            fragmentManager.beginTransaction().remove(bookFragment).commit();
        }
        bookOpenErrorDetailTextView.setText(message);
        bookOpenErrorView.setVisibility(View.VISIBLE);
    }

    private String getMessage(Throwable throwable) {
        if (throwable instanceof BookFileNotFoundException) {
            String path = ((BookFileNotFoundException) throwable).getFile().getAbsolutePath();
            return getString(R.string.bookFileNotFoundError, path);
        } else if (throwable instanceof BookInvalidException) {
            return getString(R.string.bookFileInvalidError);
        } else if (throwable.getMessage() != null && !throwable.getMessage().isEmpty()) {
            return throwable.getMessage();
        } else {
            return getString(R.string.unknownError);
        }
    }
}
