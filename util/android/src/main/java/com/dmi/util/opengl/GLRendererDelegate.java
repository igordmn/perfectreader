package com.dmi.util.opengl;

public class GLRendererDelegate implements GLRenderer {
    private final GLRenderer renderer;

    public GLRendererDelegate(GLRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void onSurfaceCreated() {
        renderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        renderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onFreeResources() {
        renderer.onFreeResources();
    }

    @Override
    public void onDrawFrame() {
        renderer.onDrawFrame();
    }
}
