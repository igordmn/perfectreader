package com.dmi.perfectreader.bookstorage;

import android.annotation.SuppressLint;

import com.dmi.perfectreader.cache.BookResourceCache;
import com.google.common.io.ByteStreams;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.readium.sdk.android.Container;
import org.readium.sdk.android.EPub3;
import org.readium.sdk.android.SpineItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

@EBean
public class EPUBBookStorage implements BookStorage {
    private static final String URL_PREFIX = "bookstorage://";

    @Bean
    protected BookResourceCache resourceCache;

    private File bookFile;
    private String[] segmentUrls;
    private int[] segmentSizes;
    private boolean loaded = false;

    public void load(File bookFile) throws IOException {
        this.bookFile = bookFile;
        String[] segmentPaths = loadSegmentPaths(bookFile);
        segmentUrls = toUrls(segmentPaths);
        segmentSizes = loadLengths(bookFile, segmentPaths);
        loaded = true;
    }

    private static String[] loadSegmentPaths(File bookFile) throws IOException {
        List<String> paths = new ArrayList<>();
        if (!bookFile.exists()) {
            throw new FileNotFoundException(format("Book file not found: %s", bookFile.getAbsolutePath()));
        }
        Container container = EPub3.openBook(bookFile.getAbsolutePath());
        try {
            org.readium.sdk.android.Package pack = container.getDefaultPackage();
            String basePath = pack.getBasePath();
            for (SpineItem spineItem : pack.getSpineItems()) {
                paths.add(basePath + spineItem.getHref());
            }
        } finally {
            EPub3.closeBook(container);
        }
        return paths.toArray(new String[paths.size()]);
    }

    private static String[] toUrls(String[] paths) {
        String[] urls = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = URL_PREFIX + paths[i];
        }
        return urls;
    }

    @SuppressLint("NewApi")
    private static int[] loadLengths(File zipFile, String[] paths) throws IOException {
        int[] fileLengths = new int[paths.length];
        try (ZipFile zip = new ZipFile(zipFile)) {
            for (int i = 0; i < paths.length; i++) {
                ZipEntry entry = zip.getEntry(paths[i]);
                if (entry == null) {
                    throw new FileNotFoundException("ZipEntry " + paths[i] + " not found");
                }
                fileLengths[i] = (int) entry.getSize();
            }
        }
        return fileLengths;
    }

    @Override
    public String[] getSegmentURLs() {
        checkState(loaded);
        return segmentUrls;
    }

    @Override
    public int[] getSegmentSizes() {
        checkState(loaded);
        return segmentSizes;
    }

    @SuppressLint("NewApi")
    @Override
    public InputStream readURL(String url) throws IOException, SecurityException {
        checkState(loaded);
        if (!url.startsWith(URL_PREFIX)) {
            throw new SecurityException();
        }
        String cacheKey = format("file: %s; url: %s; lastModified: %s",
                                 bookFile.getAbsolutePath(), url, bookFile.lastModified());
        return resourceCache.openRead(cacheKey, outputStream -> {
            if (!bookFile.exists()) {
                throw new FileNotFoundException(format("Book file not found: %s", bookFile.getAbsolutePath()));
            }
            // todo проверить производительность при открытии ZipFile каждый раз
            try (ZipFile zipFile = new ZipFile(bookFile)) {
                String filePath = url.substring(URL_PREFIX.length());
                ZipEntry entry = zipFile.getEntry(filePath);
                if (entry == null) {
                    throw new FileNotFoundException("ZipEntry " + filePath + " not found");
                }
                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    ByteStreams.copy(inputStream, outputStream);
                }
            }
        });
    }
}
