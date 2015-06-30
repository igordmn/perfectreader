package com.dmi.util.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataCache {
    InputStream openRead(String cacheKey, DataWriter dataWriter) throws IOException;

    interface DataWriter {
        void write(OutputStream outputStream) throws IOException;
    }
}
