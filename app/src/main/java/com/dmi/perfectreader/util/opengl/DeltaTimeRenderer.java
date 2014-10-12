package com.dmi.perfectreader.util.opengl;

public interface DeltaTimeRenderer {
    void setRequester(RenderRequester renderRequester);

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(float dt);
}
