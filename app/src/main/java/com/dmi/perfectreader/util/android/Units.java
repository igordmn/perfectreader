package com.dmi.perfectreader.util.android;

import android.content.Context;
import android.util.DisplayMetrics;

public class Units {
    private static DisplayMetrics displayMetrics;

    public static void init(Context context) {
        displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
    }

    public static float dipToPx(float dip) {
        return dip * displayMetrics.density;
    }

    public static float pxToDip(float px) {
        return px / displayMetrics.density;
    }
}