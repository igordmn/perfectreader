package com.dmi.util.cache;

import com.google.common.io.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// todo написать тест
public abstract class DiskDataCache implements DataCache {
    private final int maxSize;

    private File cachePath;

    protected DiskDataCache(int maxSize) {
        this.maxSize = maxSize;
    }

    protected void setCachePath(File cachePath) {
        this.cachePath = cachePath;
    }

    @Override
    public InputStream openRead(String cacheKey, DataWriter dataWriter) throws IOException {
        boolean isExist = true;

        String uuid = fetchUUID(cacheKey);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            isExist = false;
        }

        File resourceFile = new File(cachePath, uuid);
        if (!resourceFile.exists()) {
            writeFile(resourceFile, dataWriter);
        }

        long size = resourceFile.length();
        long lastAccess = new Date().getTime();

        if (isExist) {
            updateRecord(cacheKey, uuid, size, lastAccess);
        } else {
            insertRecord(cacheKey, uuid, size, lastAccess);
        }

        cleanup();

        return new BufferedInputStream(new FileInputStream(resourceFile));
    }

    private void writeFile(File file, DataWriter dataWriter) throws IOException {
        Files.createParentDirs(file);
        // подчеркивание в конце для того, чтобы при вылете приложения, не оставалось недозаписанных файлов
        // todo удлалять файлы с подчеркиванием в cleanup
        File tempFile = new File(file.getAbsolutePath() + "_");
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            dataWriter.write(os);
        }
        if (!tempFile.renameTo(file)) {
            throw new IOException("Cannot rename file " + file.getAbsolutePath());
        }
    }

    private void deleteFile(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("cannot delete file " + file.getAbsolutePath());
        }
    }

    private void cleanup() throws IOException {
        List<SizeEntry> sizeEntries = sizeEntriesOrderByLastAccess();

        int currentSize = 0;
        int deleteFromIndex = 0;
        for (SizeEntry sizeEntry : sizeEntries) {
            currentSize += sizeEntry.size;
            if (currentSize > maxSize) {
                break;
            }
            deleteFromIndex++;
        }
        if (deleteFromIndex == 0) {
            deleteFromIndex = 1;
        }

        int index = 0;
        for (SizeEntry sizeEntry : sizeEntries) {
            if (index >= deleteFromIndex) {
                deleteFile(new File(cachePath, sizeEntry.uuid));
            }
            index++;
        }

        deleteRecordsFromIndexOrderByLastAccess(deleteFromIndex);
    }

    protected abstract String fetchUUID(String key);

    protected abstract List<SizeEntry> sizeEntriesOrderByLastAccess();

    protected abstract void deleteRecordsFromIndexOrderByLastAccess(int index);

    protected abstract void insertRecord(String key, String uuid, long size, long lastAccess);

    protected abstract void updateRecord(String key, String uuid, long size, long lastAccess);

    protected static class SizeEntry {
        public String uuid;
        long size;

        public SizeEntry(String uuid, long size) {
            this.uuid = uuid;
            this.size = size;
        }
    }
}
