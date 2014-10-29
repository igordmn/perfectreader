package com.dmi.perfectreader.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.BookFragment_;
import com.dmi.perfectreader.menu.MenuActions;
import com.dmi.perfectreader.menu.MenuFragment;
import com.dmi.perfectreader.menu.MenuFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.dmi.perfectreader.book.position.Position.percentToPosition;
import static com.dmi.perfectreader.book.position.Position.toPosition;

@EActivity(R.layout.activity_book_reader)
public class MainActivity extends Activity {
    private static final int MAX_FILE_LENGTH = 1024 * 1024;

    @ViewById
    protected View bookNotLoadedView;
    @ViewById
    protected View bookTooBigView;
    @Pref
    protected StatePrefs_ statePrefs;

    private boolean bookNotLoaded;
    private boolean bookTooBig;

    @AfterViews
    protected void initViews() {
        bookNotLoadedView.setVisibility(bookNotLoaded && !bookTooBig ? View.VISIBLE : View.GONE);
        bookTooBigView.setVisibility(bookNotLoaded && bookTooBig ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String bookFile = statePrefs.bookFilePath().get();
        String intentBookFile = extractFileFromIntent();

        if (intentBookFile != null && !intentBookFile.equals(bookFile)) {
            statePrefs.bookFilePath().put(intentBookFile);
            statePrefs.bookPosition().put(0);
            bookFile = intentBookFile;
        }

        if (!bookFile.equals("")) {
            if (new File(bookFile).length() >= MAX_FILE_LENGTH) {
                bookTooBig = true;
                bookNotLoaded = true;
            } else {
                BookFragment bookFragment = BookFragment_.builder()
                        .bookFile(new File(bookFile))
                        .bookPosition(toPosition(statePrefs.bookPosition().get(), Long.MAX_VALUE))
                        .build();
                bookFragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleMenu();
                    }
                });
                getFragmentManager().beginTransaction().add(R.id.mainContainer, bookFragment, BookFragment.class.getName()).commit();
                bookTooBig = false;
                bookNotLoaded = false;
            }
        } else {
            bookNotLoaded = true;
        }
    }

    @Override
    protected void onPause() {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        if (bookFragment != null) {
            statePrefs.bookPosition().put(bookFragment.position().toLocalPosition(Long.MAX_VALUE));
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        return bookFragment.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        BookFragment bookFragment = bookFragment(getFragmentManager());
        return bookFragment.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
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
                bookFragment.goPosition(percentToPosition(percent));
            }

            @Override
            public double getPercent() {
                return bookFragment.position().percent();
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
}
