package com.dmi.util.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewExt extends GLSurfaceView {
    private GLRenderer renderer;

    private final AtomicBoolean renderRun = new AtomicBoolean(false);
    private boolean needFreeResources = false;

    public GLSurfaceViewExt(Context context) {
        super(context);
    }

    public GLSurfaceViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPause() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                queueEvent(this::freeResources);
                super.onPause();
            }
        }
    }

    @Override
    public void onResume() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                super.onResume();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        queueEvent(this::freeResources);
        super.onDetachedFromWindow();
    }

    @Override
    public void requestRender() {
        synchronized (renderRun) {
            if (renderRun.get()) {
                super.requestRender();
            }
        }
    }

    public void setRenderer(GLRenderer renderer) {
        this.renderer = renderer;
        synchronized (renderRun) {
            super.setRenderer(new Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                    needFreeResources = true;
                    renderer.onSurfaceCreated();
                }

                @Override
                public void onSurfaceChanged(GL10 gl, int width, int height) {
                    renderer.onSurfaceChanged(width, height);
                }

                @Override
                public void onDrawFrame(GL10 gl) {
                    renderer.onDrawFrame();
                }
            });
            renderRun.set(true);
        }
    }

    private void freeResources() {
        if (needFreeResources) {
            renderer.onFreeResources();
            needFreeResources = false;
        }
    }

    @Override
    public void setRenderer(Renderer renderer) {
        throw new RuntimeException("You should use setRenderer(GLRenderer)");
    }
}
