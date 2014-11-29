package com.dmi.perfectreader.util.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataCache {
    public InputStream openRead(String cacheKey, DataWriter dataWriter) throws IOException;

    public static interface DataWriter {
        void write(OutputStream outputStream) throws IOException;
    }
}
