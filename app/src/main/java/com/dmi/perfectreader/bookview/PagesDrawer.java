package com.dmi.perfectreader.bookview;

import android.graphics.Canvas;

import com.dmi.perfectreader.book.config.BookLocation;

public interface PagesDrawer {
    void drawPage(BookLocation location, int relativeIndex, Canvas canvas);
}
