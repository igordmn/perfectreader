package com.dmi.util.android.bitmap;

import android.graphics.Bitmap;

import com.dmi.util.android.paint.PaintBuffer;

public class BitmapUtils {
    public static PaintBuffer lockBuffer(Bitmap bitmap) {
        return new PaintBuffer(nativeLockBuffer(bitmap));
    }
    public static void unlockBufferAndPost(Bitmap bitmap, PaintBuffer buffer) {
        nativeUnlockBufferAndPost(bitmap, buffer.nativePtr);
    }

    private static native long nativeLockBuffer(Bitmap bitmap);
    private static native void nativeUnlockBufferAndPost(Bitmap bitmap, long paintBufferPtr);
}