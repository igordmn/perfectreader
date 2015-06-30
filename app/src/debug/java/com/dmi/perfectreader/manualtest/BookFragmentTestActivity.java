package com.dmi.perfectreader.manualtest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.book.BookFragment_;
import com.dmi.perfectreader.manualtest.testbook.TestBooks;
import com.google.common.io.ByteStreams;

import org.androidannotations.annotations.EActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@EActivity(R.layout.activity_fragment_container)
public class BookFragmentTestActivity extends FragmentActivity {
    private static final String TEST_BOOK = TestBooks.ALICE_IN_WONDERLAND;
    private BookFragment bookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = BookFragment.class.getName();
        bookFragment = (BookFragment) fragmentManager.findFragmentByTag(tag);
        if (bookFragment == null) {
            bookFragment = BookFragment_.builder()
                               .bookFile(tempBook(TEST_BOOK))
                               .build();
            fragmentManager.beginTransaction().add(R.id.mainContainer, bookFragment, tag).commit();
        }
    }

    @SuppressLint("NewApi")
    private File tempBook(String path) {
        path = path.substring("assets://".length());
        try {
            File tempFile = new File(getCacheDir(), "testbook.epub");
            try (InputStream is = getAssets().open(path)) {
                try (FileOutputStream os = new FileOutputStream(tempFile)) {
                    ByteStreams.copy(is, os);
                }
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            bookFragment.goNextPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            bookFragment.goPreviewPage();
            return  true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
               keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
               super.onKeyUp(keyCode, event);
    }
}