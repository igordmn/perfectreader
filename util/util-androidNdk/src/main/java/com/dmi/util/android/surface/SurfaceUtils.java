package com.dmi.util.android.surface;

import android.view.Surface;

import com.dmi.util.android.paint.PaintBuffer;

public class SurfaceUtils {
    public static void setBuffersGeometry(Surface surface, int width, int height) {
        nativeSetBuffersGeometry(surface, width, height);
    }

    public static PaintBuffer lockBuffer(Surface surface) {
        return new PaintBuffer(nativeLockBuffer(surface));
    }

    public static void unlockBufferAndPost(Surface surface, PaintBuffer buffer) {
        nativeUnlockBufferAndPost(surface, buffer.nativePtr);
    }

    private static native void nativeSetBuffersGeometry(Surface surface, int width, int height);
    private static native long nativeLockBuffer(Surface surface);
    private static native void nativeUnlockBufferAndPost(Surface surface, long surfaceBufferPtr);
}