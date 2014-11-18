package com.dmi.perfectreader.util.zip;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class ZipUtls {
    public static void unzipFiles(File zipFile, File outputFolder) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                String fileName = entry.getName();
                File newFile = new File(outputFolder, fileName);

                Files.createParentDirs(newFile);

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    ByteStreams.copy(zis, fos);
                }
            }
        }
    }
}
