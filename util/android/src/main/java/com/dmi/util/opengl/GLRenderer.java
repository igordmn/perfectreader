package com.dmi.util.opengl;

public interface GLRenderer {
    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    /**
     * Free resources, created in onSurfaceCreated. May be invoke multiple times after onSurfaceCreated.
     */
    void onFreeResources();

    void onDrawFrame();
}
