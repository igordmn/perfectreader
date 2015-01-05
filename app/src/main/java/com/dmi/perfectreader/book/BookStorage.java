package com.dmi.perfectreader.book;

import android.annotation.SuppressLint;

import com.dmi.perfectreader.error.BookFileNotFoundException;
import com.dmi.perfectreader.util.cache.DataCache;
import com.google.common.io.ByteStreams;

import org.readium.sdk.android.Container;
import org.readium.sdk.android.EPub3;
import org.readium.sdk.android.SpineItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// todo переместить в модуль core
public class BookStorage {
    private static final String URL_PREFIX = "bookstorage://";

    private DataCache resourceCache;

    private File bookFile;
    private final List<String> segmentUrls = new ArrayList<>();

    public BookStorage(DataCache resourceCache) {
        this.resourceCache = resourceCache;
    }

    public void load(File bookFile) throws BookFileNotFoundException {
        this.bookFile = bookFile;
        segmentUrls.clear();
        segmentUrls.addAll(getSegmentUrls(bookFile));
    }

    public List<String> segmentUrls() {
        return segmentUrls;
    }

    public boolean isBookResource(String url) {
        return url.startsWith(URL_PREFIX);
    }

    public InputStream readResource(final String url) throws IOException {
        String cacheKey = String.format("file: %s; url: %s; lastModified: %s",
                bookFile.getAbsolutePath(), url, bookFile.lastModified());
        return resourceCache.openRead(cacheKey, new DataCache.DataWriter() {
            @SuppressLint("NewApi")
            @Override
            public void write(OutputStream outputStream) throws IOException {
                if (!bookFile.exists()) {
                    throw new BookFileNotFoundException(bookFile);
                }
                // todo проверить производительность при открытии ZipFile каждый раз
                try (ZipFile zipFile = new ZipFile(bookFile)) {
                    String filePath = url.substring(URL_PREFIX.length());
                    ZipEntry entry = zipFile.getEntry(filePath);
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        ByteStreams.copy(inputStream, outputStream);
                    }
                }
            }
        });
    }

    private static List<String> getSegmentUrls(File bookFile) throws BookFileNotFoundException {
        List<String> files = new ArrayList<>();
        if (!bookFile.exists()) {
            throw new BookFileNotFoundException(bookFile);
        }
        Container container = EPub3.openBook(bookFile.getAbsolutePath());
        try {
            org.readium.sdk.android.Package pack = container.getDefaultPackage();
            File baseDir = new File(pack.getBasePath());
            for (SpineItem spineItem : pack.getSpineItems()) {
                files.add(URL_PREFIX + baseDir + "/" + spineItem.getHref());
            }
        } finally {
            EPub3.closeBook(container);
        }
        return files;
    }
}
