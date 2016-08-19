package com.dmi.util.android.graphics;

import android.graphics.Bitmap;

public class CorrectGamma {
    public static native void correctAlphaGamma(Bitmap bitmap, int x, int y, int width, int height, float gamma);
}
