package com.dmi.perfectreader.book;

import android.annotation.SuppressLint;

import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.error.BookFileNotFoundException;
import com.dmi.perfectreader.util.cache.BookResourceCache;
import com.dmi.perfectreader.util.cache.DataCache;
import com.dmi.perfectreader.util.lang.IntegerPercent;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.dmi.perfectreader.util.lang.IntegerPercent.toIntegerPercent;
import static com.google.common.base.Preconditions.checkArgument;

@EBean(scope = EBean.Scope.Singleton)
public class BookStorage {
    private static final String URL_PREFIX = "bookstorage://";

    @Bean
    protected BookResourceCache resourceCache;

    private File bookFile;
    private String[] segmentUrls;
    private long[] segmentSizes;
    private long totalSize;

    public void load(File bookFile) throws IOException {
        this.bookFile = bookFile;
        String[] segmentPaths = getSegmentPaths(bookFile);
        segmentUrls = toUrls(segmentPaths);
        segmentSizes = getLengths(bookFile, segmentPaths);
        totalSize = sum(segmentSizes);
    }

    public String[] segmentUrls() {
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
                    if (entry == null) {
                        throw new FileNotFoundException("ZipEntry " + filePath + " not found");
                    }
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        ByteStreams.copy(inputStream, outputStream);
                    }
                }
            }
        });
    }

    private static String[] getSegmentPaths(File bookFile) throws BookFileNotFoundException {
        List<String> paths = new ArrayList<>();
        if (!bookFile.exists()) {
            throw new BookFileNotFoundException(bookFile);
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

    private static String[] toUrls(String[] paths) throws BookFileNotFoundException {
        String[] urls = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = URL_PREFIX + paths[i];
        }
        return urls;
    }

    private static long[] getLengths(File zipFile, String[] paths) throws IOException {
        long[] fileLengths = new long[paths.length];
        try (ZipFile zip = new ZipFile(zipFile)) {
            for (int i = 0; i < paths.length; i++) {
                ZipEntry entry = zip.getEntry(paths[i]);
                if (entry == null) {
                    throw new FileNotFoundException("ZipEntry " + paths[i] + " not found");
                }
                fileLengths[i] = entry.getSize();
            }
        }
        return fileLengths;
    }

    private static long sum(long[] lengths) {
        long sum = 0;
        for (long length : lengths) {
            sum += length;
        }
        return sum;
    }

    public BookLocation percentToLocation(double percent) {
        checkArgument(percent >= 0 && percent <= 1.0);
        long position = (long) (percent * totalSize);
        long segmentStart, segmentEnd = 0;
        for (int i = 0; i < segmentSizes.length; i++) {
            segmentStart = segmentEnd;
            long segmentSize = segmentSizes[i];
            segmentEnd += segmentSize;
            if (position >= segmentStart && position < segmentEnd) {
                long positionInSegment = position - segmentStart;
                double segmentPercent = (double) positionInSegment / segmentSize;
                return new BookLocation(i, toIntegerPercent(segmentPercent));
            }
        }
        return new BookLocation(segmentSizes.length - 1, IntegerPercent.HUNDRED);
    }

    public double locationToPercent(BookLocation location) {
        long position = 0;
        for (int i = 0; i < location.segmentIndex() - 1; i++) {
            position += segmentSizes[i];
        }
        position += segmentSizes[location.segmentIndex()] * IntegerPercent.toDouble(location.percent());
        return (double) position / totalSize;
    }
}
