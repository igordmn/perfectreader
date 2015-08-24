package com.dmi.perfectreader.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.bookreader.BookReaderFragment;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.layout.HasLayout;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@HasLayout(R.layout.activity_container)
public class AppActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (findChild(R.id.rootContainer) == null) {
            File requestedBookFile = requestedBookFile(getIntent());
            addChild(BookReaderFragment.intent(requestedBookFile), R.id.rootContainer);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        File requestedBookFile = requestedBookFile(intent);
        if (requestedBookFile != null) {
            removeChild(R.id.rootContainer);
            addChild(BookReaderFragment.intent(requestedBookFile), R.id.rootContainer);
        }
    }

    private File requestedBookFile(Intent intent) {
        Uri data = intent.getData();
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
