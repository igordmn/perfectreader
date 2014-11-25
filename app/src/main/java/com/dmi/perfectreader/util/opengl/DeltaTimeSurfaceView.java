package com.dmi.perfectreader.util.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class DeltaTimeSurfaceView extends GLSurfaceView {
    private final static int SMOOTH_SAMPLES = 4;

    private boolean pause = true;
    private long previewTime = -1;
    private AverageValue averageDeltaTime = new AverageValue(SMOOTH_SAMPLES);

    protected DeltaTimeSurfaceView(Context context) {
        super(context);
    }

    protected DeltaTimeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void initRender() {
        setRenderer(new DeltaTimeRenderer());
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void pauseDrawing() {
        pause = true;
    }

    public void resumeDrawing() {
        if (pause) {
            pause = false;
            previewTime = -1;
            requestRender();
        }
    }

    protected abstract void onSurfaceCreated();

    protected abstract void onSurfaceChanged(int width, int height);

    protected abstract void onDrawFrame(float dt);

    private class DeltaTimeRenderer implements Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            DeltaTimeSurfaceView.this.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            DeltaTimeSurfaceView.this.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            DeltaTimeSurfaceView.this.onDrawFrame(deltaTimeSeconds());
            if (!pause) {
                requestRender();
            }
        }
    }

    private float deltaTimeSeconds() {
        long nowTime = System.currentTimeMillis();
        if (previewTime != -1) {
            float deltaTimeSeconds = (nowTime - previewTime) / 1e3f;
            averageDeltaTime.put(deltaTimeSeconds);
        } else {
            averageDeltaTime.reset();
        }
        previewTime = nowTime;
        return averageDeltaTime.average();
    }

    private static class AverageValue {
        private final int samples;
        private final float[] values;

        private int offset = 0;
        private int count = 0;

        private AverageValue(int samples) {
            this.samples = samples;
            values = new float[samples];
        }

        public void put(float value) {
            values[offset++] = value;
            if (offset == samples) {
                offset = 0;
            }
            if (count < samples) {
                count++;
            }
        }

        public void reset() {
            offset = 0;
            count = 0;
        }

        public float average() {
            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += values[i];
            }
            return count > 0 ? sum / count : 0;
        }
    }
}
