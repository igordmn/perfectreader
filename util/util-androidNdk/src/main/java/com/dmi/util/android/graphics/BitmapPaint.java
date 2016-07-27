package com.dmi.util.android.graphics;

import android.graphics.Bitmap;

public class BitmapPaint {
    public static void lockBuffer(PixelBuffer buffer, Bitmap bitmap) {
        buffer.nativePtr = nativeLockBuffer(bitmap);
    }

    public static void unlockBufferAndPost(PixelBuffer buffer, Bitmap bitmap) {
        nativeUnlockBufferAndPost(bitmap, buffer.nativePtr);
        buffer.nativePtr = 0;
    }

    private static native long nativeLockBuffer(Bitmap bitmap);
    private static native void nativeUnlockBufferAndPost(Bitmap bitmap, long pixelBufferPtr);
}