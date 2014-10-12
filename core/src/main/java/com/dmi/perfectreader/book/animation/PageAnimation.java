package com.dmi.perfectreader.book.animation;

public interface PageAnimation {
    void setPageWidth(float pageWidth);

    boolean isPagesMoving();

    void reset();

    void moveNext();

    void movePreview();

    void update(float dt);

    void drawPages(PageDrawer pageDrawer, float screenWidth);

    public static interface PageDrawer {
        void drawPage(int relativeIndex, float posX);
    }
}
