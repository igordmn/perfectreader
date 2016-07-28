package com.dmi.util.android.test;

public class PaintUtilsTestJNI {
    public static native int argb2abgr(int argb);

    public static native void copyPixelsAlphaBlend(
            int dstWidth, int dstHeight, int dstStride, int[] dstData,
            int srcWidth, int srcHeight, int srcStride, byte[] srcData,
            int x, int y, int color
    );

    public static native void copyPixels(
            int dstWidth, int dstHeight, int dstStride, byte[] dstData,
            int srcWidth, int srcHeight, int srcStride, byte[] srcData,
            int x, int y
    );

    public static native void clear(int srcWidth, int srcHeight, int srcStride, byte[] srcData, byte alpha);

    public static native int gaussianBlurAdditionalPixels(float radius);

    public static native void boxBlurHorizontal(
            int dstWidth, int dstHeight, int dstStride, byte[] dstData, float radius
    );

    public static native void boxBlurVertical(
            int dstWidth, int dstHeight, int dstStride, byte[] dstData, float radius
    );
}