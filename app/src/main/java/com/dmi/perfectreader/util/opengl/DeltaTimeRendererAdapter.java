package com.dmi.perfectreader.util.opengl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DeltaTimeRendererAdapter implements GLSurfaceView.Renderer, RenderRequester {
    private final GLSurfaceView glSurfaceView;
    private final DeltaTimeRenderer deltaTimeRenderer;

    private long previewTime = -1;

    public DeltaTimeRendererAdapter(GLSurfaceView glSurfaceView, DeltaTimeRenderer deltaTimeRenderer) {
        this.glSurfaceView = glSurfaceView;
        this.deltaTimeRenderer = deltaTimeRenderer;
        deltaTimeRenderer.setRequester(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        deltaTimeRenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        deltaTimeRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float deltaTimeSeconds = deltaTimeSeconds();
        deltaTimeRenderer.onDrawFrame(deltaTimeSeconds);
    }

    private float deltaTimeSeconds() {
        long nowTime = System.currentTimeMillis();
        return previewTime != -1 ? (nowTime - previewTime) / 1e3f : 0;
    }

    @Override
    public void requestRender() {
        previewTime = System.currentTimeMillis();
        glSurfaceView.requestRender();
    }
}
