package com.dmi.perfectreader.bookview;

import android.graphics.Canvas;

public interface PagesDrawer {
    void drawPages(int relativeIndex, Canvas canvas);

    boolean canDraw();
}
