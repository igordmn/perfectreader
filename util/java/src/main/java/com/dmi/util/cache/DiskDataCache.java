package com.dmi.util.cache;


import com.dmi.util.io.InputStreamWrapper;
import com.google.common.base.Charsets;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.google.common.hash.Hashing.sha1;

public class DiskDataCache implements DataCache, Closeable {
    private final DiskLruCache diskLruCache;

    public DiskDataCache(File directory, int appVersion, int maxSize) throws IOException {
        diskLruCache = DiskLruCache.open(directory, appVersion, 1, maxSize);
    }

    @Override
    public void close() throws IOException {
        diskLruCache.close();
    }

    @Override
    public InputStream openRead(String key, DataWriter dataWriter) throws IOException {
        key = hashKey(key);
        DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
        if (snapshot != null) {
            return new SnapshotInputStream(snapshot, 0);
        } else {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            try (OutputStream os = editor.newOutputStream(0)) {
                dataWriter.write(os);
            }
            editor.commit();
            diskLruCache.flush();
            return new SnapshotInputStream(diskLruCache.get(key), 0);
        }
    }

    private String hashKey(String key) {
        return sha1().hashString(key, Charsets.UTF_8).toString();
    }

    private static class SnapshotInputStream extends InputStreamWrapper {
        private final DiskLruCache.Snapshot snapshot;

        private SnapshotInputStream(DiskLruCache.Snapshot snapshot, int index) {
            super(snapshot.getInputStream(index));
            this.snapshot = snapshot;
        }

        @Override
        public void close() throws IOException {
            super.close();
            snapshot.close();
        }
    }
}
