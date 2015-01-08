package com.dmi.perfectreader.bookview;

import android.graphics.Canvas;

public interface PagesDrawer {
    void drawPage(int relativeIndex, Canvas canvas);

    BatchDraw batchDraw();

    interface BatchDraw {
        boolean endDraw();
    }
}
