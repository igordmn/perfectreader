package com.dmi.perfectreader.book.pagebook;

public interface PageBook {
    void resize(int width, int height);

    CanGoResult canGoPage(int offset);

    void tap(float x, float y, float tapDiameter);

    void goPercent(double percent);

    void goNextPage();

    void goPreviewPage();

    enum CanGoResult {
        /* Next/preview page is loaded */
        CAN,

        /* On first or last page and cannot go preview/next page */
        CANNOT,

        /* Next/preview page is loading and it is not known whether it is possible go page */
        UNKNOWN
    }
}
