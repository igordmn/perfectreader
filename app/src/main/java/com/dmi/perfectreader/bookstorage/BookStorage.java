package com.dmi.perfectreader.bookstorage;

import java.io.IOException;
import java.io.InputStream;

public interface BookStorage {
    String[] getSegmentURLs();

    int[] getSegmentSizes();

    InputStream readURL(String url) throws IOException, SecurityException;
}
