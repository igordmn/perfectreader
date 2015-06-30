package com.dmi.perfectreader.bookreader;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.BookFragment_;
import com.dmi.perfectreader.db.Databases;
import com.dmi.perfectreader.facade.BookFacade;
import com.dmi.perfectreader.facade.BookReaderFacade;
import com.dmi.perfectreader.userdata.UserData;
import com.dmi.util.AppCompatActivityExt;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@EActivity(R.layout.activity_book_reader)
public class BookReaderActivity extends AppCompatActivityExt implements BookReaderFacade {
    @Bean
    protected UserData userData;
    @Bean
    protected Databases databases;

    private BookFragment bookFragment;

    private File bookFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databases.registerClient();
        bookFile = getBookFile();
        initFragments();
    }

    private File getBookFile() {
        File requestedBookFile = getRequestedBookFile();
        if (requestedBookFile != null) {
            userData.saveLastBookFile(requestedBookFile);
            return requestedBookFile;
        } else {
            return userData.loadLastBookFile();
        }
    }

    private void initFragments() {
        initBookFragment();
    }

    private void initBookFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = BookFragment.class.getName();
        bookFragment = (BookFragment) fragmentManager.findFragmentByTag(tag);
        if (bookFragment == null) {
            bookFragment = BookFragment_.builder()
                                        .bookFile(bookFile)
                                        .build();
            fragmentManager.beginTransaction().add(R.id.bookFragmentContainer, bookFragment, tag).commit();
        }
    }

    @Override
    protected void onDestroy() {
        databases.unregisterClient();
        super.onDestroy();
    }

    @Override
    public void toggleMenu() {
        // временно отключено для того, чтобы не вошло в версию 0.3
        Log.d(BookReaderActivity.class.getName(), "Menu not implemented");
        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = MenuFragment.class.getName();
        MenuFragment fragment = (MenuFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = MenuFragment_.builder().build();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .add(R.id.subFragmentContainer, fragment, tag)
                    .commit();
        } else {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .remove(fragment)
                    .commit();
        }
        */
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public BookFacade book() {
        return bookFragment;
    }

    private File getRequestedBookFile() {
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
}
