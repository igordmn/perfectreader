package com.dmi.perfectreader.manualtest.book;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.manualtest.testbook.TestBooks;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.layout.HasLayout;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@HasLayout(R.layout.activity_container)
public class BookFragmentTestActivity extends BaseActivity {
    private static final String TEST_BOOK = TestBooks.PRATCHETT_INTERESTING_TIMES;
    private BookFragment bookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        bookFragment = findOrAddChild(BookFragment.intent(tempBook(TEST_BOOK)), R.id.rootContainer);
    }

    @SuppressLint("NewApi")
    private File tempBook(String path) {
        path = path.substring("assets://".length());
        try {
            String fileName = new File(path).getName();
            File tempFile = new File(getCacheDir(),fileName);
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
