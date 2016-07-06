package com.dmi.util.android.surface;

import android.view.Surface;

public class SurfaceUtils {
    public static void setBuffersGeometry(Surface surface, int width, int height) {
        nativeSetBuffersGeometry(surface, width, height);
    }

    public static SurfaceBuffer lockBuffer(Surface surface) {
        return new SurfaceBuffer(nativeLockBuffer(surface));
    }

    public static void unlockBufferAndPost(Surface surface, SurfaceBuffer buffer) {
        nativeUnlockBufferAndPost(surface, buffer.nativePtr);
    }

    private static native void nativeSetBuffersGeometry(Surface surface, int width, int height);
    private static native long nativeLockBuffer(Surface surface);
    private static native void nativeUnlockBufferAndPost(Surface surface, long surfaceBufferPtr);
}