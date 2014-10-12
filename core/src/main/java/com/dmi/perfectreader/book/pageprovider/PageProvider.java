package com.dmi.perfectreader.book.pageprovider;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.position.Position;

import java.io.IOException;

import javax.annotation.Nullable;

public interface PageProvider {
    Iterator iterator(Position position) throws IOException;

    interface Iterator extends AutoCloseable {
        @Nullable
        Content next() throws IOException;

        @Override
        void close() throws IOException;
    }
}
