package com.dmi.util.android.opengl;

import android.graphics.Bitmap;

public class OpenGL {
    public static native void texSubImage2D(
            int target, int level, int xoffset, int yoffset,
            Bitmap bitmap, int bitmapX, int bitmapY, int bitmapWidth, int bitmapHeight
    );
}
