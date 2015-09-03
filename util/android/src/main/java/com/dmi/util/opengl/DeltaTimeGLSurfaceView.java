package com.dmi.util.opengl;

import android.content.Context;
import android.util.AttributeSet;

import static java.lang.Math.min;

public abstract class DeltaTimeGLSurfaceView extends GLSurfaceViewExt {
    private final static int SMOOTH_SAMPLES = 8;
    private final static float MAX_DELTA_TIME_SECONDS = 1 / 20.0F;

    private long previewTime = -1;
    private AverageValue averageDeltaTime = new AverageValue(SMOOTH_SAMPLES);

    protected DeltaTimeGLSurfaceView(Context context) {
        super(context);
    }

    protected DeltaTimeGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void runRender() {
        setRenderer(new DeltaTimeRenderer());
    }

    public void resetTimer() {
        previewTime = -1;
    }

    protected void onSurfaceCreated() {}

    protected void onSurfaceChanged(int width, int height) {}

    protected void onFreeResources() {}

    protected void onUpdate(float dt) {}

    protected void onDrawFrame() {}

    private class DeltaTimeRenderer implements GLRenderer {
        @Override
        public void onSurfaceCreated() {
            DeltaTimeGLSurfaceView.this.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            DeltaTimeGLSurfaceView.this.onSurfaceChanged(width, height);
        }

        @Override
        public void onFreeResources() {
            DeltaTimeGLSurfaceView.this.onFreeResources();
        }

        @Override
        public void onDrawFrame() {
            DeltaTimeGLSurfaceView.this.onUpdate(deltaTimeSeconds());
            DeltaTimeGLSurfaceView.this.onDrawFrame();
        }
    }

    private float deltaTimeSeconds() {
        long nowTime = System.nanoTime();
        if (previewTime != -1) {
            float deltaTimeSeconds = min((nowTime - previewTime) / 1E9F, MAX_DELTA_TIME_SECONDS);
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
