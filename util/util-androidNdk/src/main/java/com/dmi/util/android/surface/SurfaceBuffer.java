package com.dmi.util.android.surface;

public class SurfaceBuffer {
    public final long nativePtr;

    public SurfaceBuffer(long nativePtr) {
        this.nativePtr = nativePtr;
    }
}