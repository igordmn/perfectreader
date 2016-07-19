package com.dmi.util.android.graphics;

import android.graphics.Bitmap;

public class BitmapPaint {
    public static void lockBuffer(PaintBuffer buffer, Bitmap bitmap) {
        if (buffer.isLocked)
            throw new IllegalStateException("Buffer is already locked");
        buffer.isLocked = true;
        buffer.nativePtr = nativeLockBuffer(bitmap);
    }

    public static void unlockBufferAndPost(PaintBuffer buffer, Bitmap bitmap) {
        if (!buffer.isLocked)
            throw new IllegalStateException("Buffer is not locked");
        nativeUnlockBufferAndPost(bitmap, buffer.nativePtr);
        buffer.nativePtr = 0;
        buffer.isLocked = false;
    }

    private static native long nativeLockBuffer(Bitmap bitmap);
    private static native void nativeUnlockBufferAndPost(Bitmap bitmap, long paintBufferPtr);
}