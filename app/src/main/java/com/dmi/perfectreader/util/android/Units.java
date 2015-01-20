package com.dmi.perfectreader.util.android;

import android.content.Context;
import android.util.DisplayMetrics;

public class Units {
    private static float density;

    public static void init(Context context) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        density = displayMetrics.density;
    }

    public static float dipToPx(float dip) {
        return dip * density;
    }

    public static float pxToDip(float px) {
        return px / density;
    }
}