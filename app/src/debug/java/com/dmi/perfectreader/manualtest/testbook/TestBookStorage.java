package com.dmi.perfectreader.manualtest.testbook;

import android.content.Context;

import com.dmi.perfectreader.bookstorage.BookStorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TestBookStorage implements BookStorage {
    private final Context context;
    private String[] segmentURLs;

    public TestBookStorage(Context context, String[] segmentURLs) {
        this.context = context;
        this.segmentURLs = segmentURLs;
    }

    @Override
    public String[] getSegmentURLs() {
        return segmentURLs;
    }

    @Override
    public int[] getSegmentSizes() {
        int count = getSegmentURLs().length;
        int[] sizes = new int[count];
        Arrays.fill(sizes, 10);
        return sizes;
    }

    @Override
    public InputStream readURL(String url) throws IOException, SecurityException {
        if (url.startsWith("assets://")) {
            return context.getAssets().open(url.substring("assets://".length()));
        } else {
            throw new SecurityException();
        }
    }
}
