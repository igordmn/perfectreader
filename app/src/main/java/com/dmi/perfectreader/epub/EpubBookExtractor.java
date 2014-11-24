package com.dmi.perfectreader.epub;

import com.dmi.perfectreader.html.HtmlBookTransformer;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.readium.sdk.android.Container;
import org.readium.sdk.android.EPub3;
import org.readium.sdk.android.SpineItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EpubBookExtractor {
    private HtmlBookTransformer htmlBookTransformer;

    public EpubBookExtractor(HtmlBookTransformer htmlBookTransformer) {
        this.htmlBookTransformer = htmlBookTransformer;
    }

    public List<String> extract(File bookFile, File outputDirectory) {
        List<File> segmentFiles = getSegmentFiles(bookFile);
        unzipBook(bookFile, outputDirectory, segmentFiles);
        return toUrls(outputDirectory, segmentFiles);
    }

    private void unzipBook(File bookFile, File outputDirectory, List<File> segmentFiles) {
        deleteDirectory(outputDirectory);
        try {
            unzipFiles(bookFile, outputDirectory, segmentFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void unzipFiles(File zipFile, File outputFolder, List<File> segmentFiles) throws IOException {
        Set<File> segmentFilesSet = new HashSet<>(segmentFiles);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                String localFileName = entry.getName();
                File localFile = new File(localFileName);
                File newFile = new File(outputFolder, localFileName);

                Files.createParentDirs(newFile);

                if (segmentFilesSet.contains(localFile)) {
                    try (OutputStream fos = new FileOutputStream(newFile)) {
                        htmlBookTransformer.transform(zis, fos);
                    }
                } else {
                    try (OutputStream fos = new FileOutputStream(newFile)) {
                        ByteStreams.copy(zis, fos);
                    }
                }
            }
        }
    }

    private static List<String> toUrls(File rootDirectory, List<File> localFiles) {
        List<String> urls = new ArrayList<>();
        for (File localFile : localFiles) {
            urls.add("file://" + new File(rootDirectory, localFile.getAbsolutePath()).getAbsolutePath());
        }
        return urls;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    private static List<File> getSegmentFiles(File bookFile) {
        List<File> files = new ArrayList<>();
        Container container = EPub3.openBook(bookFile.getAbsolutePath());
        try {
            org.readium.sdk.android.Package pack = container.getDefaultPackage();
            File baseDir = new File(pack.getBasePath());
            for (SpineItem spineItem : pack.getSpineItems()) {
                files.add(new File(baseDir, spineItem.getHref()));
            }
        } finally {
            EPub3.closeBook(container);
        }
        return files;
    }
}
