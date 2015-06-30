package com.dmi.perfectreader.book;

public interface PageBook {
    CanGoResult canGoPage(int offset);

    /**
     * @param integerPercent integer percent. see {@link com.dmi.util.lang.IntegerPercent}
     */
    void goPercent(int integerPercent);

    void goNextPage();

    void goPreviewPage();

    default void glInit() {}

    /**
     * Free resources, created in glInit. May be invoke multiple times after glInit.
     */
    default void glFreeResources() {}

    default void glSetSize(int width, int height) {}

    /**
     * @return false if now book cannot draw current state (going to page, resizing)
     */
    default boolean glCanDraw() { return true; }

    default void glDraw() {}

    enum CanGoResult {
        /* Next/preview page is loaded */
        CAN,

        /* On first or last page and cannot go preview/next page */
        CANNOT,

        /* Next/preview page is loading and it is not known whether it is possible go page */
        UNKNOWN
    }
}
