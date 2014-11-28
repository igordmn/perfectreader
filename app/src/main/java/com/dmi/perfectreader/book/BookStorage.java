package com.dmi.perfectreader.book;

import com.dmi.perfectreader.book.epub.EpubSegmentModifier;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BookStorage {
    private static final String URL_PREFIX = "bookstorage://";

    private EpubSegmentModifier epubSegmentModifier;
    private DataCache dataCache;

    private File bookFile;
    private final List<String> segmentUrls = new ArrayList<>();
    private final Set<String> segmentUrlsSet = new HashSet<>();

    public BookStorage(EpubSegmentModifier epubSegmentModifier, DataCache dataCache) {
        this.epubSegmentModifier = epubSegmentModifier;
        this.dataCache = dataCache;
    }

    public void load(File bookFile) {
        this.bookFile = bookFile;
        segmentUrls.clear();
        segmentUrlsSet.clear();
        segmentUrls.addAll(getSegmentUrls(bookFile));
        segmentUrlsSet.addAll(segmentUrls);
    }

    public int segmentCount() {
        return segmentUrls.size();
    }

    public String segmentUrl(int index) {
        return segmentUrls.get(index);
    }

    public boolean isBookResource(String url) {
        return url.startsWith(URL_PREFIX);
    }

    public InputStream readResource(final String url) throws IOException {
        final boolean isSegmentUrl = segmentUrlsSet.contains(url);
        String cacheKey = String.format("file: %s; lastModified: %s; modVersion: %s",
                bookFile.getAbsolutePath(), bookFile.lastModified(), epubSegmentModifier.version());
        return dataCache.openRead(cacheKey, new DataCache.DataWriter() {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                // todo проверить производительность при открытии ZipFile каждый раз
                try (ZipFile zipFile = new ZipFile(bookFile)) {
                    String filePath = url.substring(URL_PREFIX.length());
                    ZipEntry entry = zipFile.getEntry(filePath);
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        if (isSegmentUrl) {
                            epubSegmentModifier.modify(inputStream, outputStream);
                        } else {
                            ByteStreams.copy(inputStream, outputStream);
                        }
                    }
                }
            }
        });
    }

    private static List<String> getSegmentUrls(File bookFile) {
        List<String> files = new ArrayList<>();
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
