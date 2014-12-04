package com.dmi.perfectreader.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
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
import com.dmi.perfectreader.menu.MenuActions;
import com.dmi.perfectreader.menu.MenuFragment;
import com.dmi.perfectreader.menu.MenuFragment_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@EActivity(R.layout.activity_book_reader)
public class MainActivity extends Activity {
    @ViewById
    protected View bookNotLoadedView;
    @ViewById
    protected View bookOpenErrorView;
    @ViewById
    protected TextView bookOpenErrorDetailTextView;
    @Pref
    protected StatePrefs_ statePrefs;
    @Bean
    protected EventBus eventBus;
    @Bean
    protected Databases databases;

    private boolean bookNotLoaded;

    @AfterViews
    protected void initViews() {
        bookNotLoadedView.setVisibility(bookNotLoaded ? View.VISIBLE : View.GONE);
        bookOpenErrorView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        databases.registerClient();
    }

    @Subscribe
    public void onApplicationInitFinish(ApplicationInitFinishEvent event) {
        String bookFile = getBookFile();
        openBook(bookFile);
    }

    private String getBookFile() {
        String bookFile = statePrefs.bookFilePath().get();
        String intentBookFile = extractFileFromIntent();

        if (intentBookFile != null && !intentBookFile.equals(bookFile)) {
            statePrefs.bookFilePath().put(intentBookFile);
            statePrefs.bookPosition().put(0);
            bookFile = intentBookFile;
        }

        return bookFile;
    }

    private void openBook(String bookFile) {
        if (!bookFile.equals("")) {
            BookFragment bookFragment = BookFragment_.builder()
                    .bookFile(new File(bookFile))
                    .build();
            bookFragment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMenu();
                }
            });
            getFragmentManager().beginTransaction().add(R.id.mainContainer, bookFragment, BookFragment.class.getName()).commit();
            bookNotLoaded = false;
        } else {
            bookNotLoaded = true;
        }
    }

    @Override
    protected void onDestroy() {
        databases.unregisterClient();
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        if (bookFragment != null) {
            statePrefs.bookPosition().put(0);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        return bookFragment != null && bookFragment.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        return bookFragment != null && bookFragment.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
    }

    private void toggleMenu() {
        FragmentManager fragmentManager = getFragmentManager();
        String tag = MenuFragment.class.getName();
        MenuFragment fragment = (MenuFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = buildMenuFragment(fragmentManager);
            fragmentManager.beginTransaction().add(R.id.mainContainer, fragment, tag).commit();
        } else {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    private MenuFragment buildMenuFragment(FragmentManager fragmentManager) {
        final BookFragment bookFragment = bookFragment(fragmentManager);
        MenuFragment fragment = MenuFragment_.builder().build();
        fragment.setMenuActions(new MenuActions() {
            @Override
            public void goPercent(double percent) {
            }

            @Override
            public double getPercent() {
                return 0;
            }
        });
        return fragment;
    }

    private BookFragment bookFragment(FragmentManager fragmentManager) {
        return (BookFragment) fragmentManager.findFragmentByTag(BookFragment.class.getName());
    }

    private String extractFileFromIntent() {
        Uri data = getIntent().getData();
        try {
            return data != null ? URLDecoder.decode(data.getEncodedPath(), "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent) {
        showError(getMessage(errorEvent.getThrowable()));
    }

    private void showError(String message) {
        FragmentManager fragmentManager = getFragmentManager();
        final BookFragment bookFragment = bookFragment(fragmentManager);
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
