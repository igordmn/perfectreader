package com.dmi.perfectreader.util.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataCache {
    public InputStream openRead(String cacheKey, DataWriter dataWriter) throws IOException {
        // todo заглушка, изменить
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataWriter.write(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static interface DataWriter {
        void write(OutputStream outputStream) throws IOException;
    }
}
