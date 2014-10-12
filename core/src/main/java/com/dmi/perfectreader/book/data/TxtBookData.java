package com.dmi.perfectreader.book.data;

import com.dmi.perfectreader.book.position.Position;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class TxtBookData implements BookData {
    private final File file;

    private StringBookData textBookData;

    public TxtBookData(File file) {
        this.file = file;
    }

    @Override
    public ItemReader itemReader(final Position position) throws IOException {
        checkTextRead();
        return textBookData.itemReader(position);
    }

    @Override
    public ItemReader influenceItemReader(Position position) throws IOException {
        checkTextRead();
        return textBookData.influenceItemReader(position);
    }

    private void checkTextRead() throws IOException {
        if (textBookData == null) {
            String text = Files.toString(file, Charsets.UTF_8)
                    .replace("\r\n", "\n")
                    .replace("\t", "    ")
                    .replace((char) 65533, '?');
            textBookData = new StringBookData(text);
        }
    }
}
