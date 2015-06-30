package com.dmi.perfectreader.facade;

public interface BookFacade {
    int currentPercent();

    /**
     * @param tapHandler called if tap not handled by BookFragment (clicked not on link, button etc)
     */
    void tap(float x, float y, float tapDiameter, TapHandler tapHandler);

    void goPercent(int percent);

    void goNextPage();

    void goPreviewPage();

    interface TapHandler {
        void handleTap();
    }
}
