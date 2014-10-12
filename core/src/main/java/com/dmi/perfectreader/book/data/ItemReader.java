package com.dmi.perfectreader.book.data;

import com.dmi.perfectreader.book.item.ItemHandler;

import java.io.IOException;

public interface ItemReader {
    boolean hasItems() throws IOException;

    void nextItem(ItemHandler itemHandler) throws IOException;
}
