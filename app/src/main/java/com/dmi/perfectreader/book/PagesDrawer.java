package com.dmi.perfectreader.book;

import android.graphics.Canvas;

public interface PagesDrawer {
    void drawPage(BookLocation location, int relativeIndex, Canvas canvas);
}
