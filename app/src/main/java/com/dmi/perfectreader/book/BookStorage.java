package com.dmi.perfectreader.book;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class BookStorage {
    private File storageFolder;

    public void setStorageFolder(File storageFolder) {
        this.storageFolder = storageFolder;
    }

    public URLConnection connectResource(String localUrl) throws IOException {
        URL url = toURL(localUrl);
        return url.openConnection();
    }

    private URL toURL(String localUrl) {
        try {
            return new URL("file://" + new File(storageFolder, localUrl).getAbsolutePath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
