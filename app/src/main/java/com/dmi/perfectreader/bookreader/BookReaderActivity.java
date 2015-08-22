package com.dmi.perfectreader.bookreader;

import android.net.Uri;
import android.os.Bundle;

import com.dmi.perfectreader.R;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.layout.HasLayout;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@HasLayout(R.layout.activity_container)
public class BookReaderActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findOrAddChild(BookReaderFragment.intent(getRequestedBookFile()), R.id.mainContainer);
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
