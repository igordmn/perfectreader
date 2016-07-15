package com.dmi.util.android.paint;

public class PaintUtils {
    public static void fillColor(PaintBuffer paintBuffer, int color) {
        fillColor(paintBuffer.nativePtr, color);
    }

    private static native void fillColor(long paintBufferPtr, int color);
}